import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const ProtectedRoute = ({ role }) => {
  const { user } = useAuth();

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (role && user.role !== role && user.role !== 'ADMIN') { // Admin usually has access to everything or specific admin routes
      // If user is ADMIN trying to access VOTER route, it's usually fine or we redirect to admin dashboard
      if (role === 'VOTER' && user.role === 'ADMIN') {
          return <Navigate to="/admin" replace />;
      }
      return <Navigate to="/dashboard" replace />;
  }

  return <Outlet />;
};

export default ProtectedRoute;
