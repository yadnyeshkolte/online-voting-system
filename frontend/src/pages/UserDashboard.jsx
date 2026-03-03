import { useEffect, useState, useRef } from 'react';
import { Container, Row, Col, Card, Button, Badge, Modal } from 'react-bootstrap';
import Webcam from 'react-webcam';
import { getActiveElections, getCompletedElections, getCandidates, castVote, checkHasVoted, getUserElectionResults } from '../services/api';
import { useAuth } from '../context/AuthContext';

const UserDashboard = () => {
    const [elections, setElections] = useState([]);
    const [completedElections, setCompletedElections] = useState([]);
    const [selectedElection, setSelectedElection] = useState(null);
    const [candidates, setCandidates] = useState([]);
    const [hasVotedMap, setHasVotedMap] = useState({});
    const [results, setResults] = useState([]);
    const [showResults, setShowResults] = useState(false);
    
    const webcamRef = useRef(null);
    const [isVerifying, setIsVerifying] = useState(false);
    const [capturedImageBlob, setCapturedImageBlob] = useState(null);
    const [capturedImagePreview, setCapturedImagePreview] = useState('');
    const [cameraError, setCameraError] = useState('');
    const [videoConstraints, setVideoConstraints] = useState({ facingMode: { ideal: "user" } });
    const [retriedConstraints, setRetriedConstraints] = useState(false);

    const { user } = useAuth();

    useEffect(() => {
        loadElections();
    }, []);

    const loadElections = async () => {
        const data = await getActiveElections();
        setElections(data);

        const completedData = await getCompletedElections();
        setCompletedElections(completedData);

        // Check voting status for each election
        const votedStatus = {};
        for (const election of data) {
            const hasVoted = await checkHasVoted(election.electionId);
            votedStatus[election.electionId] = hasVoted;
        }
        setHasVotedMap(votedStatus);
    };

    const handleSelectElection = async (election) => {
        setSelectedElection(election);
        setCapturedImageBlob(null);
        setCapturedImagePreview('');
        setVideoConstraints({ facingMode: { ideal: "user" } });
        setRetriedConstraints(false);
        if (!window.isSecureContext) {
            setCameraError('This page is not secure. Camera works only on HTTPS or localhost.');
        } else {
            setCameraError('');
        }
        const cands = await getCandidates(election.electionId);
        setCandidates(cands);
    };

    // Convert base64 to blob
    const dataURItoBlob = (dataURI) => {
        if (!dataURI) return null;
        const byteString = atob(dataURI.split(',')[1]);
        const mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0];
        const ab = new ArrayBuffer(byteString.length);
        const ia = new Uint8Array(ab);
        for (let i = 0; i < byteString.length; i++) {
            ia[i] = byteString.charCodeAt(i);
        }
        return new Blob([ab], { type: mimeString });
    };

    const capture = () => {
        const imageSrc = webcamRef.current?.getScreenshot();
        if (!imageSrc) {
            setCameraError('Could not capture image from webcam. Please allow camera access and ensure HTTPS/localhost is used.');
            return;
        }

        const blob = dataURItoBlob(imageSrc);
        if (!blob) {
            setCameraError('Could not process captured image. Please try again.');
            return;
        }

        setCapturedImageBlob(blob);
        setCapturedImagePreview(imageSrc);
        setCameraError('');
    };

    const closeVotingModal = () => {
        setSelectedElection(null);
        setCapturedImageBlob(null);
        setCapturedImagePreview('');
        setCameraError('');
        setIsVerifying(false);
    };

    const handleCameraError = (err) => {
        const errorName = err?.name || 'UnknownError';
        let message = 'Camera access failed. Please check browser camera permissions.';

        if (errorName === 'NotAllowedError' || errorName === 'PermissionDeniedError') {
            message = 'Camera permission denied. Allow camera access in browser site settings and reload.';
        } else if (errorName === 'NotFoundError' || errorName === 'DevicesNotFoundError') {
            message = 'No camera device found on this system.';
        } else if (errorName === 'NotReadableError' || errorName === 'TrackStartError') {
            message = 'Camera is busy (used by another app/tab). Close other apps using camera and try again.';
        } else if (errorName === 'OverconstrainedError' || errorName === 'ConstraintNotSatisfiedError') {
            message = 'Requested camera mode is not available on this device.';
        } else if (!window.isSecureContext) {
            message = 'This page is not secure. Camera works only on HTTPS or localhost.';
        }

        // Retry once with no facing-mode constraint for wider device compatibility.
        if ((errorName === 'OverconstrainedError' || errorName === 'ConstraintNotSatisfiedError') && !retriedConstraints) {
            setRetriedConstraints(true);
            setVideoConstraints(undefined);
            setCameraError(`${message} Retrying with default camera settings...`);
            return;
        }

        setCameraError(`${message} (${errorName})`);
    };

    const handleVote = async (candidateId) => {
        if (!capturedImageBlob) {
            alert('Please capture your image for verification before voting.');
            return;
        }

        if (window.confirm('Are you sure you want to cast your vote?')) {
            try {
                setIsVerifying(true);
                await castVote(selectedElection.electionId, candidateId, capturedImageBlob);
                alert('Vote cast successfully!');
                loadElections(); // Refresh status
                closeVotingModal();
            } catch (e) {
                alert('Voting failed: ' + (e.response?.data || e.message));
            } finally {
                setIsVerifying(false);
            }
        }
    };

    const handleViewResults = async (election) => {
        // Allow viewing results (maybe for completed or active if allowed)
        // For now assumes we can view it.
        const res = await getUserElectionResults(election.electionId);
        setResults(res);
        setSelectedElection(election);
        setShowResults(true);
    };

    // --- GRADIENT THEME CONSTANTS ---
    const primaryGradient = 'linear-gradient(135deg, #6a11cb 0%, #2575fc 100%)'; 
    const lightGradient = 'linear-gradient(135deg, #e0c3fc 0%, #8ec5fc 100%)';

    return (
        <Container className="mt-4">

            <div style={{ 
                background: primaryGradient, 
                color: 'white', 
                padding: '25px 0',      /* Reduced from 50px to 25px */
                marginBottom: '25px',   /* Reduced spacing below the box */
                textAlign: 'center',
                boxShadow: '0 4px 12px rgba(0,0,0,0.1)',
                borderRadius: '13px 13px 13px 13px' /* Optional: soft rounding at the bottom */
            }}>
                <Container>
                    <h2 style={{ fontWeight: '700', margin: '0' }}>Voter Dashboard</h2>
                    <p style={{ opacity: '0.8', fontSize: '1rem', margin: '5px 0 0' }}>
                        User ID: {user?.id}
                    </p>
                </Container>
            </div>

            <h4 className="mt-4">Active Elections</h4>
            {elections.length === 0 && <p>No active elections at the moment.</p>}

            <Row>
                {elections.map(e => (
                    <Col md={4} key={e.electionId} className="mb-3">
                        <Card style={{ border: 'none', borderRadius: '15px', overflow: 'hidden', boxShadow: '0 8px 20px rgba(0,0,0,0.08)' }}>
                            <div style={{ background: primaryGradient, height: '8px' }}></div>
                            <Card.Body>
                                <Card.Title className="fw-bold">{e.electionName}</Card.Title>
                                <Card.Subtitle className="mb-3 text-muted">{e.electionType}</Card.Subtitle>
                                <Card.Text>
                                    Status: <Badge style={{ background: '#28a745' }}>{e.status}</Badge>
                                </Card.Text>
                                {hasVotedMap[e.electionId] ? (
                                    <Button variant="secondary" className="w-100 py-2" disabled>You Voted</Button>
                                ) : (
                                    <Button 
                                        style={{ background: primaryGradient, border: 'none' }} 
                                        className="w-100 py-2 shadow-sm" 
                                        onClick={() => handleSelectElection(e)}
                                    >
                                        Vote Now
                                    </Button>
                                )}
                            </Card.Body>
                        </Card>
                    </Col>
                ))}
            </Row>

            <h4 className="mt-4">Past Election Results</h4>
            {completedElections.length === 0 && <p>No completed elections found.</p>}

            <Row>
                {completedElections.map(e => (
                    <Col md={4} key={e.electionId} className="mb-4">
                        <Card style={{ 
                            border: 'none', 
                            borderRadius: '15px', 
                            boxShadow: '0 4px 12px rgba(0,0,0,0.05)',
                            transition: 'transform 0.2s' 
                        }}>
                            <Card.Body>
                                <Card.Title className="fw-bold">{e.electionName}</Card.Title>
                                <Card.Subtitle className="mb-3 text-muted">{e.electionType}</Card.Subtitle>
                                <div className="d-flex justify-content-between align-items-center">
                                    <Badge bg="secondary" pill style={{ fontSize: '0.8rem' }}>{e.status}</Badge>
                                    <Button 
                                        variant="link" 
                                        style={{ 
                                            color: '#6a11cb', // Matching the violet in your gradient
                                            fontWeight: '600',
                                            textDecoration: 'none'
                                        }} 
                                        onClick={() => handleViewResults(e)}
                                    >
                                        View Results →
                                    </Button>
                                </div>
                            </Card.Body>
                        </Card>
                    </Col>
                ))}
            </Row>

            {/* Voting Modal */}
            <Modal show={!!selectedElection && !showResults} onHide={closeVotingModal} size="lg">
                <Modal.Header 
                    closeButton 
                    closeVariant="white" /* This ensures the cross is white like the Results modal */
                    style={{ background: primaryGradient, color: 'white' }}
                >
                    <Modal.Title style={{ fontWeight: '600' }}>
                        Cast Your Vote: {selectedElection?.electionName}
                    </Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <div className="mb-4 text-center">
                        <h5>Face Verification Required</h5>
                        <p className="text-muted small">Capture your face clearly before selecting a candidate.</p>
                        {cameraError && (
                            <div className="alert alert-warning py-2 text-start">
                                {cameraError}
                            </div>
                        )}
                        <Webcam
                            audio={false}
                            ref={webcamRef}
                            screenshotFormat="image/jpeg"
                            width={320}
                            height={240}
                            onUserMedia={() => setCameraError('')}
                            onUserMediaError={handleCameraError}
                            videoConstraints={videoConstraints}
                            style={{ borderRadius: '10px', border: '2px solid #ddd' }}
                        />
                        <div className="mt-3 d-flex justify-content-center gap-2">
                            {!capturedImageBlob ? (
                                <Button variant="outline-primary" onClick={capture}>
                                    Capture Photo
                                </Button>
                            ) : (
                                <Button
                                    variant="outline-secondary"
                                    onClick={() => {
                                        setCapturedImageBlob(null);
                                        setCapturedImagePreview('');
                                    }}
                                >
                                    Retake Photo
                                </Button>
                            )}
                        </div>
                        {capturedImagePreview && (
                            <div className="mt-3">
                                <p className="text-muted small mb-2">Captured preview</p>
                                <img
                                    src={capturedImagePreview}
                                    alt="Captured for verification"
                                    style={{ width: '160px', height: '120px', objectFit: 'cover', borderRadius: '8px', border: '1px solid #ddd' }}
                                />
                            </div>
                        )}
                    </div>
                    <hr />
                    <h5>Candidates</h5>
                    <Row>
                        {candidates.map(c => (
                            <Col md={6} key={c.candidateId} className="mb-3">
                                <Card className="h-100">
                                    <Card.Body className="text-center">
                                        <div style={{marginBottom: '10px'}}>
                                            {c.candidatePhoto ? (
                                                <img
                                                    src={`data:image/jpeg;base64,${c.candidatePhoto}`}
                                                    alt="Candidate"
                                                    style={{width: '100px', height: '100px', objectFit: 'cover', borderRadius: '50%'}}
                                                />
                                            ) : (
                                                <div style={{fontSize: '2rem'}}>👤</div>
                                            )}
                                        </div>
                                        <Card.Title>{c.user?.fullName}</Card.Title>
                                        <Card.Subtitle>{c.partyName} ({c.partySymbol})</Card.Subtitle>
                                        <Card.Text className="mt-2 text-muted small">
                                            "{c.manifesto}"
                                        </Card.Text>
                                        <Button 
                                            style={{ background: primaryGradient, border: 'none' }} 
                                            className="w-100 mt-2"
                                            onClick={() => handleVote(c.candidateId)}
                                            disabled={isVerifying || !capturedImageBlob}
                                        >
                                            {isVerifying ? 'Verifying...' : `Vote for ${c.partySymbol}`}
                                        </Button>
                                    </Card.Body>
                                </Card>
                            </Col>
                        ))}
                    </Row>
                </Modal.Body>
            </Modal>

             {/* Results Modal Reuse */}
            <Modal show={showResults} onHide={() => {setShowResults(false); setSelectedElection(null);}} size="lg">
            <Modal.Header 
                closeButton 
                closeVariant="white" /* This turns the dark cross white */
                style={{ background: primaryGradient, color: 'white', borderBottom: 'none' }}
            >
                <Modal.Title style={{ fontWeight: '600' }}>
                    Election Results: {selectedElection?.electionName}
                </Modal.Title>
            </Modal.Header>
                <Modal.Body>
                    <table className="table">
                        <thead>
                            <tr>
                                <th>Rank</th>
                                <th>Candidate</th>
                                <th>Party</th>
                                <th>Votes</th>
                                <th>Percentage</th>
                            </tr>
                        </thead>
                        <tbody>
                            {results.map(r => (
                                <tr key={r.resultId}>
                                    <td>{r.rankPosition}</td>
                                    <td>{r.candidate?.user?.fullName}</td>
                                    <td>{r.candidate?.partyName} ({r.candidate?.partySymbol})</td>
                                    <td>{r.voteCount}</td>
                                    <td>{r.votePercentage}%</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </Modal.Body>
            </Modal>
        </Container>
    );
};

export default UserDashboard;
