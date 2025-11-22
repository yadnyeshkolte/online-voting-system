import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useParams } from 'react-router-dom';

const AddCandidate = () => {
  const { electionId } = useParams();
  const [candidates, setCandidates] = useState([]);
  const [formData, setFormData] = useState({
    partyName: '',
    candidateSymbol: '',
    candidatePhotoUrl: ''
  });

  useEffect(() => {
    fetchCandidates();
  }, [electionId]);

  const fetchCandidates = async () => {
    const res = await axios.get(`/candidates/election/${electionId}`);
    setCandidates(res.data);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await axios.post(`/candidates/election/${electionId}`, formData);
      setFormData({ partyName: '', candidateSymbol: '', candidatePhotoUrl: '' });
      fetchCandidates();
    } catch (err) {
      alert('Failed to add candidate');
    }
  };

  return (
    <div>
        <h3>Manage Candidates</h3>
        <div className="row">
            <div className="col-md-4">
                <div className="card">
                    <div className="card-body">
                        <h5>Add New</h5>
                        <form onSubmit={handleSubmit}>
                            <div className="mb-3">
                                <label>Party Name</label>
                                <input className="form-control" value={formData.partyName} onChange={e => setFormData({...formData, partyName: e.target.value})} required />
                            </div>
                            <div className="mb-3">
                                <label>Symbol (URL/Name)</label>
                                <input className="form-control" value={formData.candidateSymbol} onChange={e => setFormData({...formData, candidateSymbol: e.target.value})} required />
                            </div>
                             <div className="mb-3">
                                <label>Photo URL</label>
                                <input className="form-control" value={formData.candidatePhotoUrl} onChange={e => setFormData({...formData, candidatePhotoUrl: e.target.value})} />
                            </div>
                            <button className="btn btn-primary">Add</button>
                        </form>
                    </div>
                </div>
            </div>
            <div className="col-md-8">
                <div className="row">
                    {candidates.map(c => (
                        <div className="col-md-4 mb-3" key={c.candidateId}>
                            <div className="card">
                                <div className="card-body text-center">
                                    <h5>{c.partyName}</h5>
                                    <p>Symbol: {c.candidateSymbol}</p>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    </div>
  );
};

export default AddCandidate;
