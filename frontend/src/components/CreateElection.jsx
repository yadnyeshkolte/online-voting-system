import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const CreateElection = () => {
  const [formData, setFormData] = useState({
    electionName: '',
    electionType: 'GENERAL',
    startDate: '',
    endDate: ''
  });
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await axios.post('/elections', formData);
      navigate('/admin');
    } catch (err) {
      alert('Failed to create election');
    }
  };

  return (
    <div className="card">
        <div className="card-header">Create Election</div>
        <div className="card-body">
            <form onSubmit={handleSubmit}>
                <div className="mb-3">
                    <label>Name</label>
                    <input className="form-control" value={formData.electionName} onChange={e => setFormData({...formData, electionName: e.target.value})} required />
                </div>
                <div className="mb-3">
                    <label>Type</label>
                    <select className="form-control" value={formData.electionType} onChange={e => setFormData({...formData, electionType: e.target.value})}>
                        <option value="GENERAL">General</option>
                        <option value="STATE">State</option>
                        <option value="LOCAL">Local</option>
                    </select>
                </div>
                <div className="mb-3">
                    <label>Start Date</label>
                    <input type="datetime-local" className="form-control" value={formData.startDate} onChange={e => setFormData({...formData, startDate: e.target.value})} required />
                </div>
                <div className="mb-3">
                    <label>End Date</label>
                    <input type="datetime-local" className="form-control" value={formData.endDate} onChange={e => setFormData({...formData, endDate: e.target.value})} required />
                </div>
                <button className="btn btn-primary">Create</button>
            </form>
        </div>
    </div>
  );
};

export default CreateElection;
