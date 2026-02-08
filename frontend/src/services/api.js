import axios from 'axios';

// Use environment variable for API URL in production, or relative path /api for dev proxy
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api';

const api = axios.create({
    baseURL: API_BASE_URL
});

api.interceptors.request.use((config) => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

export const loginUser = async (identifier, password) => {
    const response = await api.post('/auth/login', { identifier, password });
    return response.data;
};

export const registerUser = async (userData) => {
    const response = await api.post('/auth/register', userData);
    return response.data;
};

export const getActiveElections = async () => {
    const response = await api.get('/user/elections/active');
    return response.data;
};

export const getCompletedElections = async () => {
    const response = await api.get('/user/elections/completed');
    return response.data;
};

export const getCandidates = async (electionId) => {
    const response = await api.get(`/user/elections/${electionId}/candidates`);
    return response.data;
};

export const castVote = async (electionId, candidateId, capturedImageBlob) => {
    const formData = new FormData();
    formData.append('candidateId', candidateId);
    if (capturedImageBlob) {
        // Create a File object from the blob if possible, or just append blob
        // Give it a filename so the backend treats it as a file
        formData.append('capturedImage', capturedImageBlob, "capture.jpg");
    }

    const response = await api.post(`/user/elections/${electionId}/vote`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
    });
    return response.data;
};

export const checkHasVoted = async (electionId) => {
    const response = await api.get(`/user/elections/${electionId}/has-voted`);
    return response.data;
};

export const getUserElectionResults = async (electionId) => {
    const response = await api.get(`/user/elections/${electionId}/results`);
    return response.data;
};

// Admin APIs
export const getAllElections = async () => {
    const response = await api.get('/admin/elections');
    return response.data;
};

export const createElection = async (electionData) => {
    const response = await api.post('/admin/elections', electionData);
    return response.data;
};

export const updateElectionStatus = async (electionId, status) => {
    const response = await api.put(`/admin/elections/${electionId}/status`, null, {
        params: { status }
    });
    return response.data;
};

export const addCandidate = async (electionId, candidateData) => {
    const response = await api.post(`/admin/elections/${electionId}/candidates`, candidateData);
    return response.data;
};

export const calculateResults = async (electionId) => {
    const response = await api.post(`/admin/elections/${electionId}/calculate-results`);
    return response.data;
};

export const getElectionResults = async (electionId) => {
    const response = await api.get(`/admin/elections/${electionId}/results`);
    return response.data;
};

export const adminGetCandidates = async (electionId) => {
    const response = await api.get(`/admin/elections/${electionId}/candidates`);
    return response.data;
};

export const togglePublishResult = async (electionId) => {
    const response = await api.put(`/admin/elections/${electionId}/publish`);
    return response.data;
};

// Public APIs
export const getPublishedResults = async () => {
    const response = await api.get('/public/elections/results');
    return response.data;
};

// User Profile
export const getUserProfile = async () => {
    const response = await api.get('/user/profile');
    return response.data;
};

export const updateUserProfile = async (profileData) => {
    const response = await api.put('/user/profile', profileData);
    return response.data;
};

// Admin User Management
export const searchUsers = async (query) => {
    const response = await api.get('/admin/users', { params: { query } });
    return response.data;
};

export const adminUpdateUser = async (userId, userData) => {
    const response = await api.put(`/admin/users/${userId}`, userData);
    return response.data;
};

export const uploadProfilePhoto = async (imageFile) => {
    const formData = new FormData();
    formData.append('file', imageFile);
    const response = await api.post('/user/profile/photo', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
    });
    return response.data;
};

export const updateCandidateImage = async (electionId, candidateId, imageFile) => {
    const formData = new FormData();
    formData.append('file', imageFile);
    const response = await api.post(`/admin/elections/${electionId}/candidates/${candidateId}/photo`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
    });
    return response.data;
};
