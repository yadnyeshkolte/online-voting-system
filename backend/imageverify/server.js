const express = require('express');
const multer = require('multer');
const faceapi = require('face-api.js');
const canvas = require('canvas');
const fs = require('fs');
const path = require('path');
const cors = require('cors');

const app = express();
const port = process.env.PORT || 5001; // Environment variable or default
let modelsLoaded = false;

app.use(cors());
app.use(express.json());

// Configure face-api to use canvas
const { Canvas, Image, ImageData } = canvas;
faceapi.env.monkeyPatch({ Canvas, Image, ImageData });

// Configure multer for file uploads
const upload = multer({ dest: 'uploads/' });

// Load models
async function loadModels() {
    const modelPath = path.join(__dirname, 'models');
    await faceapi.nets.ssdMobilenetv1.loadFromDisk(modelPath);
    await faceapi.nets.faceLandmark68Net.loadFromDisk(modelPath);
    await faceapi.nets.faceRecognitionNet.loadFromDisk(modelPath);
    modelsLoaded = true;
    console.log('FaceAPI models loaded');
}

app.get('/health', (_req, res) => {
    if (!modelsLoaded) {
        return res.status(503).json({ status: 'starting' });
    }
    return res.json({ status: 'ok' });
});

app.post('/verify', upload.fields([{ name: 'storedImage', maxCount: 1 }, { name: 'capturedImage', maxCount: 1 }]), async (req, res) => {
    if (!modelsLoaded) {
        return res.status(503).json({ error: 'Face models are still loading. Please retry.' });
    }
    try {
        if (!req.files || !req.files.storedImage || !req.files.capturedImage) {
            return res.status(400).json({ error: 'Both storedImage and capturedImage are required.' });
        }

        const storedImagePath = req.files.storedImage[0].path;
        const capturedImagePath = req.files.capturedImage[0].path;

        // Load images using canvas
        const storedImage = await canvas.loadImage(storedImagePath);
        const capturedImage = await canvas.loadImage(capturedImagePath);

        // Detect faces
        // Using SSD Mobilenet V1 for better accuracy than TinyFaceDetector
        const storedDetection = await faceapi.detectSingleFace(storedImage).withFaceLandmarks().withFaceDescriptor();
        const capturedDetection = await faceapi.detectSingleFace(capturedImage).withFaceLandmarks().withFaceDescriptor();

        // Clean up uploaded files
        fs.unlinkSync(storedImagePath);
        fs.unlinkSync(capturedImagePath);

        if (!storedDetection) {
            return res.status(400).json({ error: 'No face detected in stored profile image.' });
        }
        if (!capturedDetection) {
            return res.status(400).json({ error: 'No face detected in captured image.' });
        }

        const distance = faceapi.euclideanDistance(storedDetection.descriptor, capturedDetection.descriptor);
        
        // Threshold is usually 0.6, but lowering to 0.4 for stricter security
        const threshold = 0.4;
        const isMatch = distance < threshold;

        console.log(`Verification result: Distance = ${distance}, Threshold = ${threshold}, Match = ${isMatch}`);

        res.json({ match: isMatch, distance: distance });

    } catch (error) {
        console.error(error);
        res.status(500).json({ error: 'Internal server error during face verification.' });
    }
});

app.listen(port, () => {
    console.log(`Image Verification Service running on http://localhost:${port}`);
});

loadModels().catch((error) => {
    console.error('Failed to load FaceAPI models:', error);
    process.exit(1);
});
