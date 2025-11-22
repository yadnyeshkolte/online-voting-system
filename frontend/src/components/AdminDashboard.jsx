import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const AdminDashboard = () => {
  const [elections, setElections] = useState([]);
  const { logout } = useAuth();

  useEffect(() => {
    fetchElections();
  }, []);

  const fetchElections = async () => {
    try {
      const res = await axios.get('/elections/all');
      setElections(res.data);
    } catch (err) {
      console.error(err);
    }
  };

  const updateStatus = async (id, status) => {
      try {
          await axios.put(`/elections/${id}/status?status=${status}`);
          fetchElections();
      } catch (err) {
          alert('Failed to update status');
      }
  };

  const calculateResults = async (id) => {
      try {
          await axios.post(`/results/calculate/${id}`);
          alert('Results calculated!');
      } catch(err) {
          alert('Failed to calculate results');
      }
  };

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2>Admin Dashboard</h2>
        <button className="btn btn-danger" onClick={logout}>Logout</button>
      </div>
      <Link to="/admin/create-election" className="btn btn-success mb-3">Create New Election</Link>

      <table className="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Type</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {elections.map(e => (
            <tr key={e.electionId}>
              <td>{e.electionId}</td>
              <td>{e.electionName}</td>
              <td>{e.electionType}</td>
              <td>{e.status}</td>
              <td>
                <Link to={`/admin/add-candidate/${e.electionId}`} className="btn btn-sm btn-info me-2">Candidates</Link>
                {e.status === 'DRAFT' && <button onClick={() => updateStatus(e.electionId, 'SCHEDULED')} className="btn btn-sm btn-warning me-2">Schedule</button>}
                {e.status === 'SCHEDULED' && <button onClick={() => updateStatus(e.electionId, 'ACTIVE')} className="btn btn-sm btn-primary me-2">Start</button>}
                {e.status === 'ACTIVE' && <button onClick={() => updateStatus(e.electionId, 'COMPLETED')} className="btn btn-sm btn-danger me-2">End</button>}
                {e.status === 'COMPLETED' && (
                    <>
                        <button onClick={() => calculateResults(e.electionId)} className="btn btn-sm btn-dark me-2">Calculate Results</button>
                        <Link to={`/admin/results/${e.electionId}`} className="btn btn-sm btn-secondary">View Results</Link>
                    </>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default AdminDashboard;
