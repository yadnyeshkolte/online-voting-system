import React, { createContext, useState, useContext, useEffect } from 'react';
import axios from 'axios';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  // Configure axios defaults
  axios.defaults.baseURL = 'http://localhost:8080/api';
  axios.defaults.withCredentials = true; // For session cookies

  const checkUser = async () => {
    try {
      const response = await axios.get('/auth/me');
      setUser(response.data);
    } catch (error) {
      setUser(null);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    checkUser();
  }, []);

  const login = async (email, password) => {
    // Using Basic Auth
    const token = btoa(`${email}:${password}`);
    const config = {
      headers: { Authorization: `Basic ${token}` }
    };

    try {
        // We call /me to verify credentials and establish session
        const response = await axios.get('/auth/me', config);
        setUser(response.data);
        // After successful login, browser stores cookie/session or we can use the credential for future requests (less secure than cookie)
        // Since we enabled sessions in backend, the JSESSIONID should handle it.
        // However, we might need to send Basic Auth header once to authenticate.
        return true;
    } catch (error) {
        console.error("Login failed", error);
        return false;
    }
  };

  const logout = async () => {
      // Invalidate session
      try {
          await axios.post('/logout'); // Spring Security default logout endpoint
      } catch(e) {
          // ignore
      }
      setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, login, logout, loading }}>
      {!loading && children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
