import 'bootstrap/dist/css/bootstrap.min.css';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './components/Login';
import Register from './components/Register';
import AdminDashboard from './components/AdminDashboard';
import UserDashboard from './components/UserDashboard';
import CreateElection from './components/CreateElection';
import AddCandidate from './components/AddCandidate';
import ElectionResults from './components/ElectionResults';
import ProtectedRoute from './components/ProtectedRoute';
import { AuthProvider } from './context/AuthContext';
import './App.css';

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="container mt-4">
          <Routes>
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />

            <Route element={<ProtectedRoute role="ADMIN" />}>
               <Route path="/admin" element={<AdminDashboard />} />
               <Route path="/admin/create-election" element={<CreateElection />} />
               <Route path="/admin/add-candidate/:electionId" element={<AddCandidate />} />
               <Route path="/admin/results/:electionId" element={<ElectionResults />} />
            </Route>

            <Route element={<ProtectedRoute role="VOTER" />}>
               <Route path="/dashboard" element={<UserDashboard />} />
            </Route>

            <Route path="/" element={<Navigate to="/login" />} />
          </Routes>
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;
