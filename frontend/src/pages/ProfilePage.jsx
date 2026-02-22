import React, { useState, useEffect } from 'react';
import { getUserProfile, updateUserProfile, uploadProfilePhoto } from '../services/api';
import { Container, Row, Col, Card, Form, Button, Alert, Image } from 'react-bootstrap';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api';

const buildApiImageUrl = (path) => {
    if (!path || !path.startsWith('/api')) return path;
    if (API_BASE_URL === '/api') return path;

    const normalizedBase = API_BASE_URL.replace(/\/+$/, '');
    if (normalizedBase.endsWith('/api')) {
        return `${normalizedBase}${path.substring('/api'.length)}`;
    }
    return `${normalizedBase}${path}`;
};

const ProfilePage = () => {
    const [user, setUser] = useState(null);
    const [editMode, setEditMode] = useState(false);
    const [formData, setFormData] = useState({});
    const [profilePhotoFile, setProfilePhotoFile] = useState(null);
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');
    const [profileImageUrl, setProfileImageUrl] = useState('');

    useEffect(() => {
        fetchProfile();
    }, []);

    useEffect(() => {
        if (user?.profileImageUrl) {
            // Check if the URL is already absolute
            if (user.profileImageUrl.startsWith('http')) {
                setProfileImageUrl(user.profileImageUrl);
            } else if (user.profileImageUrl.startsWith('/api')) {
                setProfileImageUrl(`${buildApiImageUrl(user.profileImageUrl)}?t=${new Date().getTime()}`);
            } else {
                setProfileImageUrl(`${buildApiImageUrl(`/api/user/profile/photo/${user.profileImageUrl}`)}?t=${new Date().getTime()}`);
            }
        } else {
            setProfileImageUrl(buildApiImageUrl('/api/user/profile/photo/default.png'));
        }
    }, [user]);

    const fetchProfile = async () => {
        try {
            const data = await getUserProfile();
            setUser(data);
            setFormData(data);
        } catch (err) {
            setError('Failed to load profile');
        }
    };

    const handlePhotoUpload = async () => {
        if (!profilePhotoFile) {
            setError('Please select a photo to upload.');
            return;
        }
        try {
            await uploadProfilePhoto(profilePhotoFile);
            setMessage('Photo uploaded successfully!');
            setError('');
            // Refresh profile to get new image URL
            fetchProfile();
        } catch (err) {
            setError('Failed to upload photo.');
            setMessage('');
        }
    };

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await updateUserProfile(formData);
            setMessage('Profile updated successfully');
            setEditMode(false);
            fetchProfile();
        } catch (err) {
            setError('Failed to update profile');
        }
    };

    if (!user) return <div className="text-center mt-5"><div className="spinner-border text-primary" role="status"></div></div>;

    return (
        <Container className="mt-5 mb-5">
            <Row className="justify-content-center">
                <Col md={10} lg={8}>
                    <Card className="border-0 shadow-lg">
                        <Card.Header className="bg-brand-gradient text-white py-3">
                            <h3 className="mb-0 fw-bold text-center">My Profile</h3>
                        </Card.Header>
                        <Card.Body className="p-4 p-md-5">
                            {message && <Alert variant="success" className="d-flex align-items-center"><i className="bi bi-check-circle-fill me-2"></i>{message}</Alert>}
                            {error && <Alert variant="danger" className="d-flex align-items-center"><i className="bi bi-exclamation-triangle-fill me-2"></i>{error}</Alert>}

                            <div className="text-center mb-5">
                                <div className="position-relative d-inline-block">
                                    <Image
                                        src={profileImageUrl}
                                        onError={(e) => { e.target.src = buildApiImageUrl('/api/user/profile/photo/default.png'); }}
                                        roundedCircle
                                        className="shadow-sm border border-4 border-light"
                                        style={{ width: '160px', height: '160px', objectFit: 'cover' }}
                                    />
                                    {editMode && (
                                        <div className="mt-3 p-3 bg-light rounded shadow-sm border">
                                            <Form.Group>
                                                <Form.Label className="small text-muted mb-2">Change Profile Photo</Form.Label>
                                                <Form.Control type="file" size="sm" onChange={(e) => setProfilePhotoFile(e.target.files[0])} />
                                                <Button size="sm" variant="outline-primary" className="mt-2 w-100" onClick={handlePhotoUpload}>
                                                    <i className="bi bi-upload me-1"></i> Upload Photo
                                                </Button>
                                            </Form.Group>
                                        </div>
                                    )}
                                </div>
                            </div>

                            <Form onSubmit={handleSubmit}>
                                <h5 className="mb-3 text-muted border-bottom pb-2">Personal Information</h5>
                                <Row>
                                    <Col md={6}>
                                        <Form.Group className="mb-3">
                                            <Form.Label className="fw-bold">Full Name</Form.Label>
                                            <Form.Control type="text" value={user.fullName} readOnly className="bg-light" />
                                        </Form.Group>
                                    </Col>
                                    <Col md={6}>
                                        <Form.Group className="mb-3">
                                            <Form.Label className="fw-bold">Email</Form.Label>
                                            <Form.Control
                                                type="email"
                                                name="email"
                                                value={editMode ? formData.email : user.email}
                                                onChange={handleChange}
                                                readOnly={!editMode}
                                                className={!editMode ? "bg-light" : ""}
                                            />
                                        </Form.Group>
                                    </Col>
                                </Row>

                                <Row>
                                    <Col md={6}>
                                        <Form.Group className="mb-3">
                                            <Form.Label className="fw-bold">Phone Number</Form.Label>
                                            <Form.Control
                                                type="text"
                                                name="phoneNumber"
                                                value={editMode ? formData.phoneNumber : user.phoneNumber}
                                                onChange={handleChange}
                                                readOnly={!editMode}
                                                className={!editMode ? "bg-light" : ""}
                                            />
                                        </Form.Group>
                                    </Col>
                                    <Col md={6}>
                                        <Form.Group className="mb-3">
                                            <Form.Label className="fw-bold">Address</Form.Label>
                                            <Form.Control
                                                as="textarea"
                                                rows={1}
                                                name="address"
                                                value={editMode ? formData.address : user.address}
                                                onChange={handleChange}
                                                readOnly={!editMode}
                                                className={!editMode ? "bg-light" : ""}
                                            />
                                        </Form.Group>
                                    </Col>
                                </Row>

                                <Row>
                                    <Col md={4}>
                                        <Form.Group className="mb-3">
                                            <Form.Label className="fw-bold">City</Form.Label>
                                            <Form.Control
                                                type="text"
                                                name="city"
                                                value={editMode ? formData.city : user.city}
                                                onChange={handleChange}
                                                readOnly={!editMode}
                                                className={!editMode ? "bg-light" : ""}
                                            />
                                        </Form.Group>
                                    </Col>
                                    <Col md={4}>
                                        <Form.Group className="mb-3">
                                            <Form.Label className="fw-bold">State</Form.Label>
                                            <Form.Control
                                                type="text"
                                                name="state"
                                                value={editMode ? formData.state : user.state}
                                                onChange={handleChange}
                                                readOnly={!editMode}
                                                className={!editMode ? "bg-light" : ""}
                                            />
                                        </Form.Group>
                                    </Col>
                                    <Col md={4}>
                                        <Form.Group className="mb-3">
                                            <Form.Label className="fw-bold">Pincode</Form.Label>
                                            <Form.Control
                                                type="text"
                                                name="pincode"
                                                value={editMode ? formData.pincode : user.pincode}
                                                onChange={handleChange}
                                                readOnly={!editMode}
                                                className={!editMode ? "bg-light" : ""}
                                            />
                                        </Form.Group>
                                    </Col>
                                </Row>

                                <h5 className="mb-3 mt-4 text-muted border-bottom pb-2">Identity Verification</h5>
                                <Row>
                                    <Col md={6}>
                                        <Form.Group className="mb-3">
                                            <Form.Label className="fw-bold">Aadhar Number</Form.Label>
                                            <Form.Control type="text" value={user.aadharNumber} readOnly className="bg-light text-muted" />
                                        </Form.Group>
                                    </Col>
                                    <Col md={6}>
                                        <Form.Group className="mb-3">
                                            <Form.Label className="fw-bold">Voter ID Number</Form.Label>
                                            <Form.Control type="text" value={user.voterIdNumber} readOnly className="bg-light text-muted" />
                                        </Form.Group>
                                    </Col>
                                </Row>

                                <Row>
                                    <Col md={6}>
                                        <Form.Group className="mb-3">
                                            <Form.Label className="fw-bold">PAN Card Number</Form.Label>
                                            <Form.Control
                                                type="text"
                                                name="panCardNumber"
                                                value={editMode ? (formData.panCardNumber || '') : (user.panCardNumber || 'Not Added')}
                                                onChange={handleChange}
                                                readOnly={!editMode || user.panCardNumber}
                                                placeholder={!user.panCardNumber && editMode ? "Enter PAN Card Number" : ""}
                                                className={(!editMode || user.panCardNumber) ? "bg-light text-muted" : ""}
                                            />
                                            {user.panCardNumber && editMode && <Form.Text className="text-muted">Verified ID cannot be changed.</Form.Text>}
                                        </Form.Group>
                                    </Col>
                                    <Col md={6}>
                                        <Form.Group className="mb-3">
                                            <Form.Label className="fw-bold">Passport Number</Form.Label>
                                            <Form.Control
                                                type="text"
                                                name="passportNumber"
                                                value={editMode ? (formData.passportNumber || '') : (user.passportNumber || 'Not Added')}
                                                onChange={handleChange}
                                                readOnly={!editMode || user.passportNumber}
                                                placeholder={!user.passportNumber && editMode ? "Enter Passport Number" : ""}
                                                className={(!editMode || user.passportNumber) ? "bg-light text-muted" : ""}
                                            />
                                            {user.passportNumber && editMode && <Form.Text className="text-muted">Verified ID cannot be changed.</Form.Text>}
                                        </Form.Group>
                                    </Col>
                                </Row>

                                <div className="mt-4 pt-3 border-top d-flex justify-content-end gap-2">
                                    {editMode ? (
                                        <>
                                            <Button variant="outline-secondary" onClick={() => { setEditMode(false); setFormData(user); }}>Cancel</Button>
                                            <Button className="btn-brand" type="submit">Save Changes</Button>
                                        </>
                                    ) : (
                                        <Button className="btn-brand px-4" onClick={() => setEditMode(true)}>
                                            <i className="bi bi-pencil-square me-2"></i>Edit Profile
                                        </Button>
                                    )}
                                </div>
                            </Form>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default ProfilePage;
