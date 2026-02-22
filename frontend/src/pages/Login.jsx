import { useState, useEffect } from 'react';
import { Form, Button, Container, Alert, Card, Row, Col } from 'react-bootstrap';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { loginUser } from '../services/api';

const Login = () => {
    const [voterId, setVoterId] = useState('');
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
            const data = await loginUser(voterId, password);
            
            if (data.role === 'ADMIN') {
                setError('Please use the Admin Login page.');
                setIsLoading(false);
                return;
            }

            login(data.token);
            navigate('/user');
        } catch (err) {
            const serverMessage = typeof err.response?.data === 'string'
                ? err.response.data
                : err.response?.data?.message;
            if (!err.response) {
                setError('Cannot reach server. Please check backend service and HTTPS configuration.');
            } else {
                setError(serverMessage || 'Invalid credentials. Please check your Voter ID and password.');
            }
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="d-flex align-items-center justify-content-center min-vh-100 bg-light py-5 animate-fade-in">
            <Container>
                <Row className="justify-content-center">
                    <Col md={6} lg={5}>
                        <Card className="form-card shadow-lg animate-slide-up">
                            <div className="form-header">
                                <h3 className="mb-0 fw-bold">Voter Login</h3>
                                <p className="mb-0 text-white-50">Login with your Voter ID</p>
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
                                        <Form.Label className="fw-bold">Voter ID</Form.Label>
                                        <Form.Control
                                            type="text"
                                            placeholder="Enter your Voter ID"
                                            value={voterId}
                                            onChange={(e) => setVoterId(e.target.value)}
                                            required
                                            size="lg"
                                            className="bg-light border-0"
                                        />
                                    </Form.Group>

                                    <Form.Group className="mb-4">
                                        <Form.Label className="fw-bold">Password</Form.Label>
                                        <Form.Control
                                            type="password"
                                            placeholder="Enter your password"
                                            value={password}
                                            onChange={(e) => setPassword(e.target.value)}
                                            required
                                            size="lg"
                                            className="bg-light border-0"
                                        />
                                    </Form.Group>

                                    <Button 
                                        variant="primary" 
                                        type="submit" 
                                        className="w-100 mb-3 btn-lg fw-bold" 
                                        disabled={isLoading}
                                        style={{ background: 'linear-gradient(to right, #667eea, #764ba2)', border: 'none' }}
                                    >
                                        {isLoading ? 'Logging in...' : 'Login'}
                                    </Button>
                                    
                                    <div className="text-center mt-3">
                                        <span className="text-muted">Don't have an account? </span>
                                        <Link to="/register" className="fw-bold text-decoration-none text-primary">Register Here</Link>
                                    </div>
                                    <div className="text-center mt-2">
                                        <Link to="/admin-login" className="text-muted text-decoration-none small">Admin Login</Link>
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

export default Login;
