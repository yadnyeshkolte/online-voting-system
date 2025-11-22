import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useParams } from 'react-router-dom';

const ElectionResults = () => {
  const { electionId } = useParams();
  const [results, setResults] = useState([]);

  useEffect(() => {
    fetchResults();
  }, [electionId]);

  const fetchResults = async () => {
    try {
      const res = await axios.get(`/results/${electionId}`);
      setResults(res.data);
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <div>
      <h3>Election Results</h3>
      <table className="table table-striped">
        <thead>
          <tr>
            <th>Rank</th>
            <th>Party</th>
            <th>Symbol</th>
            <th>Votes</th>
            <th>Percentage</th>
          </tr>
        </thead>
        <tbody>
          {results.map(r => (
            <tr key={r.resultId}>
              <td>{r.rankPosition}</td>
              <td>{r.candidate.partyName}</td>
              <td>{r.candidate.candidateSymbol}</td>
              <td>{r.voteCount}</td>
              <td>{r.votePercentage}%</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default ElectionResults;
