import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate, Link } from 'react-router-dom';

const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const { login } = useAuth();
  const navigate = useNavigate();
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    const success = await login(email, password);
    if (success) {
      // Need to fetch user again to check role, or rely on login updating context
      // Assuming login updates context, we need to wait a bit or check user from context (which is async)
      // For simplicity, we can reload or rely on redirect in protected route?
      // Better: let login return the user object or role.
      // But context update triggers re-render.
      // Let's just navigate to dashboard, ProtectedRoute will handle redirect if role mismatch or not loaded yet?
      // Actually login function in AuthContext awaits for /auth/me, so user state is set.
      navigate('/dashboard');
    } else {
      setError('Invalid credentials');
    }
  };

  return (
    <div className="row justify-content-center">
      <div className="col-md-6">
        <div className="card">
          <div className="card-header">Login</div>
          <div className="card-body">
            {error && <div className="alert alert-danger">{error}</div>}
            <form onSubmit={handleSubmit}>
              <div className="mb-3">
                <label>Email</label>
                <input type="email" className="form-control" value={email} onChange={e => setEmail(e.target.value)} required />
              </div>
              <div className="mb-3">
                <label>Password</label>
                <input type="password" className="form-control" value={password} onChange={e => setPassword(e.target.value)} required />
              </div>
              <button type="submit" className="btn btn-primary">Login</button>
            </form>
            <div className="mt-3">
                <Link to="/register">Register here</Link>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Login;
