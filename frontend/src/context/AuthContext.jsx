import { createContext, useContext, useEffect, useState } from 'react';
import api from '../services/api';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const stored = localStorage.getItem('hilti-user');
    return stored ? JSON.parse(stored) : null;
  });

  useEffect(() => {
    if (user) {
      localStorage.setItem('hilti-user', JSON.stringify(user));
      localStorage.setItem('hilti-token', user.token);
      api.defaults.headers.common.Authorization = `Bearer ${user.token}`;
    } else {
      localStorage.removeItem('hilti-user');
      localStorage.removeItem('hilti-token');
      delete api.defaults.headers.common.Authorization;
    }
  }, [user]);

  const login = async (email, password) => {
    const response = await api.post('/api/auth/login', { email, password });
    const payload = response.data;
    const userState = {
      token: payload.token,
      email: payload.email,
      role: payload.role,
      userId: payload.userId
    };
    setUser(userState);
    return userState;
  };

  const logout = () => {
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
