import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const Register = () => {
  const [formData, setFormData] = useState({
    email: '',
    passwordHash: '',
    fullName: '',
    dateOfBirth: '',
    gender: 'MALE',
    idProofType: 'AADHAR',
    idProofNumber: '',
    address: ''
  });
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await axios.post('/auth/register', formData);
      alert('Registration successful! Please login.');
      navigate('/login');
    } catch (err) {
      setError(err.response?.data || 'Registration failed');
    }
  };

  return (
    <div className="row justify-content-center">
      <div className="col-md-8">
        <div className="card">
          <div className="card-header">Register</div>
          <div className="card-body">
            {error && <div className="alert alert-danger">{error}</div>}
            <form onSubmit={handleSubmit}>
              <div className="mb-3">
                <label>Full Name</label>
                <input name="fullName" className="form-control" onChange={handleChange} required />
              </div>
              <div className="mb-3">
                <label>Email</label>
                <input type="email" name="email" className="form-control" onChange={handleChange} required />
              </div>
              <div className="mb-3">
                <label>Password</label>
                <input type="password" name="passwordHash" className="form-control" onChange={handleChange} required />
              </div>
              <div className="mb-3">
                <label>Date of Birth</label>
                <input type="date" name="dateOfBirth" className="form-control" onChange={handleChange} required />
              </div>
              <div className="mb-3">
                <label>Gender</label>
                <select name="gender" className="form-control" onChange={handleChange}>
                    <option value="MALE">Male</option>
                    <option value="FEMALE">Female</option>
                    <option value="OTHER">Other</option>
                </select>
              </div>
              <div className="mb-3">
                <label>ID Proof Type</label>
                <select name="idProofType" className="form-control" onChange={handleChange}>
                    <option value="AADHAR">Aadhar</option>
                    <option value="PAN">PAN</option>
                    <option value="VOTER_ID">Voter ID</option>
                    <option value="PASSPORT">Passport</option>
                </select>
              </div>
              <div className="mb-3">
                <label>ID Proof Number</label>
                <input name="idProofNumber" className="form-control" onChange={handleChange} required />
                <small className="text-muted">Must match dummy records for verification (e.g., 123456789012 for Aadhar)</small>
              </div>
              <div className="mb-3">
                <label>Address</label>
                <textarea name="address" className="form-control" onChange={handleChange} />
              </div>
              <button type="submit" className="btn btn-primary">Register</button>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Register;
