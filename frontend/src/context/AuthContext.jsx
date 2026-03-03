import { createContext, useState, useEffect, useContext } from 'react';
import { jwtDecode } from 'jwt-decode';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const token = localStorage.getItem('token');
        if (token) {
            try {
                const decoded = jwtDecode(token);
                // Check expiry
                if (decoded.exp * 1000 < Date.now()) {
                    logout();
                } else {
                    setUser({
                        sub: decoded.sub,
                        role: decoded.role,
                        id: decoded.id
                    });
                }
            } catch (error) {
                console.error("Invalid token", error);
                logout();
            }
        }
        setLoading(false);
    }, []);

    const login = (token, authData = null) => {
        localStorage.setItem('token', token);
        if (authData?.role && authData?.id) {
            setUser({
                sub: authData.sub || authData.identifier || '',
                role: authData.role,
                id: authData.id
            });
            return;
        }

        try {
            const decoded = jwtDecode(token);
            setUser({
                sub: decoded.sub,
                role: decoded.role,
                id: decoded.id
            });
        } catch (error) {
            console.error("Failed to decode token during login", error);
            logout();
            throw new Error("Invalid token received from server.");
        }
    };

    const logout = () => {
        localStorage.removeItem('token');
        setUser(null);
    };

    return (
        <AuthContext.Provider value={{ user, login, logout, loading }}>
            {!loading && children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);
