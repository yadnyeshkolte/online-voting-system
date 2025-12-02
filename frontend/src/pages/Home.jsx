import { Container, Button, Row, Col, Card } from 'react-bootstrap';
import { Link } from 'react-router-dom';

const Home = () => {
    return (
        <div>
            {/* Hero Section */}
            <div className="bg-light p-5 mb-5 text-center">
                <Container>
                    <h1 className="display-4 fw-bold">Online Voting System</h1>
                    <p className="lead mb-4">
                        Experience the future of democracy. Secure, transparent, and accessible voting from anywhere.
                    </p>
                    <div>
                        <Button as={Link} to="/login" variant="primary" size="lg" className="me-3 px-4">
                            Login
                        </Button>
                        <Button as={Link} to="/register" variant="outline-primary" size="lg" className="px-4">
                            Register
                        </Button>
                    </div>
                </Container>
            </div>

            {/* Features Section */}
            <Container className="mb-5">
                <Row className="g-4">
                    <Col md={4}>
                        <Card className="h-100 border-0 shadow-sm">
                            <Card.Body className="text-center">
                                <div className="mb-3 text-primary">
                                    <i className="bi bi-shield-check fs-1"></i> {/* Assuming Bootstrap Icons are available or just generic placeholder */}
                                    <h3>Secure</h3>
                                </div>
                                <Card.Text>
                                    Your vote is protected with state-of-the-art encryption and rigorous identity verification.
                                </Card.Text>
                            </Card.Body>
                        </Card>
                    </Col>
                    <Col md={4}>
                        <Card className="h-100 border-0 shadow-sm">
                            <Card.Body className="text-center">
                                <div className="mb-3 text-primary">
                                    <i className="bi bi-speedometer2 fs-1"></i>
                                    <h3>Fast & Easy</h3>
                                </div>
                                <Card.Text>
                                    Cast your vote in seconds through our intuitive and user-friendly interface.
                                </Card.Text>
                            </Card.Body>
                        </Card>
                    </Col>
                    <Col md={4}>
                        <Card className="h-100 border-0 shadow-sm">
                            <Card.Body className="text-center">
                                <div className="mb-3 text-primary">
                                    <i className="bi bi-eye fs-1"></i>
                                    <h3>Transparent</h3>
                                </div>
                                <Card.Text>
                                    Real-time result calculation and reporting ensures complete transparency in the electoral process.
                                </Card.Text>
                            </Card.Body>
                        </Card>
                    </Col>
                </Row>
            </Container>

             {/* Footer Section */}
             <footer className="bg-dark text-white text-center py-4 mt-auto">
                <Container>
                    <p className="mb-0">&copy; {new Date().getFullYear()} Online Voting System. All rights reserved.</p>
                </Container>
            </footer>
        </div>
    );
};

export default Home;
