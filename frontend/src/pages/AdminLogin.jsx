import { useState, useEffect } from 'react';
import { Form, Button, Container, Alert, Card, Row, Col } from 'react-bootstrap';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { loginUser } from '../services/api';

const AdminLogin = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const { login, user } = useAuth();
    const navigate = useNavigate();
    const [isLoading, setIsLoading] = useState(false);

    useEffect(() => {
        if (user) {
            if (user.role === 'ADMIN') {
                navigate('/admin');
            } else {
                navigate('/user');
            }
        }
    }, [user, navigate]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        setError('');
        try {
            const data = await loginUser(email, password);
            
            if (data.role !== 'ADMIN') {
                setError('Access Denied. This login is for Administrators only.');
                setIsLoading(false);
                return;
            }

            login(data.token);
            navigate('/admin');
        } catch (err) {
            const serverMessage = typeof err.response?.data === 'string'
                ? err.response.data
                : err.response?.data?.message;
            if (!err.response) {
                setError('Cannot reach server. Please check backend service and HTTPS configuration.');
            } else {
                setError(serverMessage || 'Invalid credentials. Please check your email and password.');
            }
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="d-flex align-items-center justify-content-center min-vh-100 py-5 animate-fade-in">
            <Container>
                <Row className="justify-content-center">
                    <Col md={6} lg={5}>
                        <Card className="form-card shadow-lg animate-slide-up border-0">
                            <div className="form-header border-bottom border-light pb-3 mb-3">
                                <h3 className="mb-0 fw-bold text-white">Admin Portal</h3>
                                <p className="mb-0 text-white-50">Secure Login</p>
                            </div>
                            <Card.Body className="p-4 p-md-5">
                                {error && (
                                    <Alert variant="danger" className="d-flex align-items-center">
                                        <i className="bi bi-exclamation-triangle-fill me-2"></i>
                                        {error}
                                    </Alert>
                                )}
                                <Form onSubmit={handleSubmit}>
                                    <Form.Group className="mb-4">
                                        <Form.Label className="fw-bold text-dark">Email Address</Form.Label>
                                        <Form.Control
                                            type="email"
                                            placeholder="admin@example.com"
                                            value={email}
                                            onChange={(e) => setEmail(e.target.value)}
                                            required
                                            size="lg"
                                        />
                                    </Form.Group>

                                    <Form.Group className="mb-4">
                                        <Form.Label className="fw-bold text-dark">Password</Form.Label>
                                        <Form.Control
                                            type="password"
                                            placeholder="Enter your password"
                                            value={password}
                                            onChange={(e) => setPassword(e.target.value)}
                                            required
                                            size="lg"
                                        />
                                    </Form.Group>

                                    <Button 
                                        type="submit" 
                                        className="w-100 mb-3 btn-lg fw-bold btn-brand" 
                                        disabled={isLoading}
                                    >
                                        {isLoading ? 'Authenticating...' : 'Admin Login'}
                                    </Button>
                                    
                                    <div className="text-center mt-3">
                                        <Link to="/login" className="text-decoration-none small">User Login</Link>
                                    </div>
                                </Form>
                            </Card.Body>
                        </Card>
                        <div className="text-center mt-4">
                             <Link to="/" className="text-muted text-decoration-none">
                                <i className="bi bi-arrow-left me-1"></i> Back to Home
                            </Link>
                        </div>
                    </Col>
                </Row>
            </Container>
        </div>
    );
};

export default AdminLogin;
