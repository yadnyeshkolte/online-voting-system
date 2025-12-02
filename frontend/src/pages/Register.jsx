import { useState, useEffect } from 'react';
import { Form, Button, Container, Alert } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
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
        profileImageUrl: 'https://via.placeholder.com/150'
    });
    const [error, setError] = useState('');
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
        try {
            await registerUser(formData);
            navigate('/login');
        } catch (err) {
            setError(err.response?.data || 'Registration failed');
        }
    };

    return (
        <Container className="mt-5 mb-5" style={{ maxWidth: '600px' }}>
            <h2>Register</h2>
            {error && <Alert variant="danger">{error}</Alert>}
            <Form onSubmit={handleSubmit}>
                <Form.Group className="mb-3">
                    <Form.Label>Email</Form.Label>
                    <Form.Control name="email" type="email" onChange={handleChange} required />
                </Form.Group>
                <Form.Group className="mb-3">
                    <Form.Label>Password</Form.Label>
                    <Form.Control name="password" type="password" onChange={handleChange} required />
                </Form.Group>
                <Form.Group className="mb-3">
                    <Form.Label>Full Name</Form.Label>
                    <Form.Control name="fullName" type="text" onChange={handleChange} required />
                </Form.Group>
                <Form.Group className="mb-3">
                    <Form.Label>Phone Number</Form.Label>
                    <Form.Control name="phoneNumber" type="text" onChange={handleChange} />
                </Form.Group>
                <Form.Group className="mb-3">
                    <Form.Label>Date of Birth</Form.Label>
                    <Form.Control name="dateOfBirth" type="date" onChange={handleChange} required />
                </Form.Group>
                <Form.Group className="mb-3">
                    <Form.Label>Gender</Form.Label>
                    <Form.Select name="gender" onChange={handleChange}>
                        <option value="MALE">Male</option>
                        <option value="FEMALE">Female</option>
                        <option value="OTHER">Other</option>
                    </Form.Select>
                </Form.Group>
                <Form.Group className="mb-3">
                    <Form.Label>Aadhar Number</Form.Label>
                    <Form.Control name="aadharNumber" type="text" onChange={handleChange} required placeholder="12-digit Aadhar Number" />
                </Form.Group>
                <Form.Group className="mb-3">
                    <Form.Label>Voter ID Number</Form.Label>
                    <Form.Control name="voterIdNumber" type="text" onChange={handleChange} required placeholder="Voter ID Card Number" />
                </Form.Group>

                 <Form.Group className="mb-3">
                    <Form.Label>Address</Form.Label>
                    <Form.Control name="address" type="text" onChange={handleChange} />
                </Form.Group>
                 <Form.Group className="mb-3">
                    <Form.Label>City</Form.Label>
                    <Form.Control name="city" type="text" onChange={handleChange} />
                </Form.Group>
                 <Form.Group className="mb-3">
                    <Form.Label>State</Form.Label>
                    <Form.Control name="state" type="text" onChange={handleChange} />
                </Form.Group>
                 <Form.Group className="mb-3">
                    <Form.Label>Pincode</Form.Label>
                    <Form.Control name="pincode" type="text" onChange={handleChange} />
                </Form.Group>

                <Button variant="primary" type="submit" className="w-100">
                    Register
                </Button>
            </Form>
        </Container>
    );
};

export default Register;
