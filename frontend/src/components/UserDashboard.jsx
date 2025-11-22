import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useAuth } from '../context/AuthContext';

const UserDashboard = () => {
  const [activeElections, setActiveElections] = useState([]);
  const { logout, user } = useAuth();
  const [selectedElection, setSelectedElection] = useState(null);
  const [candidates, setCandidates] = useState([]);
  const [voteStatus, setVoteStatus] = useState({});

  useEffect(() => {
    fetchActiveElections();
  }, []);

  const fetchActiveElections = async () => {
    try {
      const res = await axios.get('/elections/active');
      setActiveElections(res.data);
      // Check vote status for each
      res.data.forEach(async (e) => {
          try {
             const statusRes = await axios.get(`/votes/status/${e.electionId}`);
             setVoteStatus(prev => ({...prev, [e.electionId]: statusRes.data.hasVoted}));
          } catch(err) {
              // ignore
          }
      });
    } catch (err) {
      console.error(err);
    }
  };

  const handleElectionClick = async (election) => {
      setSelectedElection(election);
      const res = await axios.get(`/candidates/election/${election.electionId}`);
      setCandidates(res.data);
  };

  const castVote = async (candidateId) => {
      if (!window.confirm("Are you sure you want to vote for this candidate?")) return;
      try {
          await axios.post('/votes/cast', {
              electionId: selectedElection.electionId,
              candidateId: candidateId
          });
          alert('Vote Cast Successfully!');
          setVoteStatus(prev => ({...prev, [selectedElection.electionId]: true}));
          setSelectedElection(null);
      } catch (err) {
          alert('Failed to cast vote: ' + (err.response?.data || err.message));
      }
  };

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2>Welcome, {user?.fullName}</h2>
        <button className="btn btn-danger" onClick={logout}>Logout</button>
      </div>

      {!selectedElection ? (
          <>
            <h4>Active Elections</h4>
            <div className="row">
                {activeElections.map(e => (
                    <div className="col-md-4 mb-3" key={e.electionId}>
                        <div className="card">
                            <div className="card-body">
                                <h5 className="card-title">{e.electionName}</h5>
                                <p className="card-text">Type: {e.electionType}</p>
                                {voteStatus[e.electionId] ? (
                                    <button className="btn btn-secondary" disabled>Voted</button>
                                ) : (
                                    <button className="btn btn-primary" onClick={() => handleElectionClick(e)}>Vote Now</button>
                                )}
                            </div>
                        </div>
                    </div>
                ))}
                {activeElections.length === 0 && <p>No active elections at the moment.</p>}
            </div>
          </>
      ) : (
          <div>
              <button className="btn btn-link mb-3" onClick={() => setSelectedElection(null)}>&larr; Back</button>
              <h4>Voting for: {selectedElection.electionName}</h4>
              <div className="row">
                  {candidates.map(c => (
                      <div className="col-md-3 mb-3" key={c.candidateId}>
                          <div className="card h-100">
                              <div className="card-body text-center">
                                  <h5>{c.partyName}</h5>
                                  <div className="display-4 mb-3">{c.candidateSymbol}</div>
                                  <button className="btn btn-success w-100" onClick={() => castVote(c.candidateId)}>Vote</button>
                              </div>
                          </div>
                      </div>
                  ))}
              </div>
          </div>
      )}
    </div>
  );
};

export default UserDashboard;
