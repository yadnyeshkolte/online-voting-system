import { useState, useEffect } from 'react';
import { Form, Button, Container, Alert, Row, Col, Card } from 'react-bootstrap';
import { useNavigate, Link } from 'react-router-dom';
import { registerUser } from '../services/api';
import { useAuth } from '../context/AuthContext';

const Register = () => {
    const [formData, setFormData] = useState({
        email: '',
        password: '',
        fullName: '',
        phoneNumber: '',
        dateOfBirth: '',
        gender: 'MALE',
        address: '',
        city: '',
        state: '',
        pincode: '',
        aadharNumber: '',
        voterIdNumber: '',
        profileImageUrl: '/api/user/profile/photo/default.png' // Default value
    });
    const [error, setError] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const navigate = useNavigate();
    const { user } = useAuth();

    useEffect(() => {
        if (user) {
            if (user.role === 'ADMIN') {
                navigate('/admin');
            } else {
                navigate('/user');
            }
        }
    }, [user, navigate]);

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        setError('');

        // Client-side age validation
        const dob = new Date(formData.dateOfBirth);
        const today = new Date();
        let age = today.getFullYear() - dob.getFullYear();
        const m = today.getMonth() - dob.getMonth();
        if (m < 0 || (m === 0 && today.getDate() < dob.getDate())) {
            age--;
        }

        if (age < 18) {
            setError('You must be at least 18 years old to register.');
            setIsLoading(false);
            return;
        }

        try {
            await registerUser(formData);
            navigate('/login');
        } catch (err) {
            setError(err.response?.data || 'Registration failed. Please check your details.');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="d-flex align-items-center min-vh-100 bg-light py-5 animate-fade-in">
            <Container>
                <Row className="justify-content-center">
                    <Col lg={10} xl={9}>
                        <Card className="form-card shadow-lg animate-slide-up">
                            <div className="form-header">
                                <h2 className="mb-0 fw-bold">Create Account</h2>
                                <p className="mb-0 text-white-50">Join the secure voting platform today</p>
                            </div>
                            <Card.Body className="p-4 p-md-5">
                                {error && <Alert variant="danger">{error}</Alert>}
                                <Form onSubmit={handleSubmit}>
                                    
                                    <h5 className="mb-3 text-primary border-bottom pb-2">Personal Information</h5>
                                    <Row className="g-3 mb-4">
                                        <Col md={6}>
                                            <Form.Group>
                                                <Form.Label className="fw-bold small">Full Name</Form.Label>
                                                <Form.Control name="fullName" type="text" onChange={handleChange} required placeholder="John Doe" />
                                            </Form.Group>
                                        </Col>
                                        <Col md={6}>
                                            <Form.Group>
                                                <Form.Label className="fw-bold small">Email Address</Form.Label>
                                                <Form.Control name="email" type="email" onChange={handleChange} required placeholder="name@example.com" />
                                            </Form.Group>
                                        </Col>
                                        <Col md={6}>
                                            <Form.Group>
                                                <Form.Label className="fw-bold small">Password</Form.Label>
                                                <Form.Control name="password" type="password" onChange={handleChange} required placeholder="Create a strong password" />
                                            </Form.Group>
                                        </Col>
                                        <Col md={6}>
                                            <Form.Group>
                                                <Form.Label className="fw-bold small">Phone Number</Form.Label>
                                                <Form.Control name="phoneNumber" type="text" onChange={handleChange} placeholder="+91 9876543210" />
                                            </Form.Group>
                                        </Col>
                                        <Col md={6}>
                                            <Form.Group>
                                                <Form.Label className="fw-bold small">Date of Birth</Form.Label>
                                                <Form.Control name="dateOfBirth" type="date" onChange={handleChange} required />
                                            </Form.Group>
                                        </Col>
                                        <Col md={6}>
                                            <Form.Group>
                                                <Form.Label className="fw-bold small">Gender</Form.Label>
                                                <Form.Select name="gender" onChange={handleChange} defaultValue="MALE">
                                                    <option value="MALE">Male</option>
                                                    <option value="FEMALE">Female</option>
                                                    <option value="OTHER">Other</option>
                                                </Form.Select>
                                            </Form.Group>
                                        </Col>
                                    </Row>

                                    <h5 className="mb-3 text-primary border-bottom pb-2">Identity Verification</h5>
                                    <Row className="g-3 mb-4">
                                        <Col md={6}>
                                            <Form.Group>
                                                <Form.Label className="fw-bold small">Aadhar Number</Form.Label>
                                                <Form.Control name="aadharNumber" type="text" onChange={handleChange} required placeholder="12-digit Aadhar Number" />
                                            </Form.Group>
                                        </Col>
                                        <Col md={6}>
                                            <Form.Group>
                                                <Form.Label className="fw-bold small">Voter ID Number</Form.Label>
                                                <Form.Control name="voterIdNumber" type="text" onChange={handleChange} required placeholder="Voter ID Card Number" />
                                            </Form.Group>
                                        </Col>
                                    </Row>

                                    <h5 className="mb-3 text-primary border-bottom pb-2">Address Details</h5>
                                    <Row className="g-3 mb-4">
                                        <Col md={12}>
                                            <Form.Group>
                                                <Form.Label className="fw-bold small">Address</Form.Label>
                                                <Form.Control name="address" type="text" onChange={handleChange} placeholder="Apartment, Studio, or Floor" />
                                            </Form.Group>
                                        </Col>
                                        <Col md={4}>
                                            <Form.Group>
                                                <Form.Label className="fw-bold small">City</Form.Label>
                                                <Form.Control name="city" type="text" onChange={handleChange} />
                                            </Form.Group>
                                        </Col>
                                        <Col md={4}>
                                            <Form.Group>
                                                <Form.Label className="fw-bold small">State</Form.Label>
                                                <Form.Control name="state" type="text" onChange={handleChange} />
                                            </Form.Group>
                                        </Col>
                                        <Col md={4}>
                                            <Form.Group>
                                                <Form.Label className="fw-bold small">Pincode</Form.Label>
                                                <Form.Control name="pincode" type="text" onChange={handleChange} />
                                            </Form.Group>
                                        </Col>
                                    </Row>

                                    <div className="d-grid gap-2 mt-4">
                                        <Button 
                                            variant="primary" 
                                            type="submit" 
                                            size="lg" 
                                            disabled={isLoading}
                                            style={{ background: 'linear-gradient(to right, #667eea, #764ba2)', border: 'none' }}
                                        >
                                            {isLoading ? 'Registering...' : 'Complete Registration'}
                                        </Button>
                                    </div>
                                    <div className="text-center mt-3">
                                        <span className="text-muted">Already have an account? </span>
                                        <Link to="/login" className="fw-bold text-decoration-none text-primary">Login Here</Link>
                                    </div>
                                </Form>
                            </Card.Body>
                        </Card>
                    </Col>
                </Row>
            </Container>
        </div>
    );
};

export default Register;
