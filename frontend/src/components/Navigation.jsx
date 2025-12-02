import { Navbar, Nav, Container, Button } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Navigation = () => {
    const { user, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <Navbar bg="dark" variant="dark" expand="lg" className="shadow-sm">
            <Container>
                <Navbar.Brand as={Link} to="/" className="fw-bold">Online Voting System</Navbar.Brand>
                <Navbar.Toggle aria-controls="basic-navbar-nav" />
                <Navbar.Collapse id="basic-navbar-nav">
                    <Nav className="me-auto">
                        {user && user.role === 'ADMIN' && (
                            <Nav.Link as={Link} to="/admin">Admin Dashboard</Nav.Link>
                        )}
                        {user && user.role === 'USER' && (
                            <>
                                <Nav.Link as={Link} to="/user">User Dashboard</Nav.Link>
                                <Nav.Link as={Link} to="/user/profile">Profile</Nav.Link>
                            </>
                        )}
                    </Nav>
                    <Nav className="align-items-center gap-2">
                        {!user ? (
                            <>
                                <Button as={Link} to="/login" variant="outline-light" size="sm" className="px-3">Login</Button>
                                <Button as={Link} to="/register" variant="primary" size="sm" className="px-3">Register</Button>
                            </>
                        ) : (
                            <Button variant="outline-danger" size="sm" onClick={handleLogout}>Logout</Button>
                        )}
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
};

export default Navigation;
