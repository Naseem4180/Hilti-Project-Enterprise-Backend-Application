import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import '../styles/Login.css';

export default function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const { login } = useAuth();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const user = await login(email, password);
      if (user.role === 'ROLE_CUSTOMER') navigate('/customer');
      if (user.role === 'ROLE_ADMIN') navigate('/admin');
      if (user.role === 'ROLE_FE') navigate('/fe');
      if (user.role === 'ROLE_MANAGER') navigate('/manager');
    } catch (err) {
      setError(err.response?.data?.message || 'Login failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleDemoLogin = async (role) => {
    const demoCredentials = {
      admin: { email: 'admin@hilti.com', password: 'admin123' },
      customer: { email: 'customer@hilti.com', password: 'customer123' },
    };

    const creds = demoCredentials[role];
    setEmail(creds.email);
    setPassword(creds.password);

    try {
      setLoading(true);
      const user = await login(creds.email, creds.password);
      navigate(role === 'admin' ? '/admin' : '/customer');
    } catch (err) {
      setError('Demo login failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <div className="login-background">
        <div className="gradient-blob blob-1"></div>
        <div className="gradient-blob blob-2"></div>
        <div className="gradient-blob blob-3"></div>
      </div>

      <div className="login-content">
        <div className="login-card">
          <div className="login-header">
            <div className="logo-large">H</div>
            <h1>Hilti Booking Portal</h1>
            <p>Equipment Rental Management System</p>
          </div>

          {error && (
            <div className="alert alert-error">
              ⚠️ {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="login-form">
            <div className="form-group">
              <label htmlFor="email">Email Address</label>
              <input
                id="email"
                type="email"
                placeholder="you@example.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="password">Password</label>
              <input
                id="password"
                type="password"
                placeholder="Enter your password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>

            <button type="submit" className="btn-submit" disabled={loading}>
              {loading ? '⏳ Signing In...' : '🔓 Sign In'}
            </button>
          </form>

          <div className="login-divider">
            <span>Or try demo account</span>
          </div>

          <div className="demo-buttons">
            <button 
              type="button"
              className="btn-demo admin"
              onClick={() => handleDemoLogin('admin')}
              disabled={loading}
            >
              👤 Admin Demo
            </button>
            <button 
              type="button"
              className="btn-demo customer"
              onClick={() => handleDemoLogin('customer')}
              disabled={loading}
            >
              👥 Customer Demo
            </button>
          </div>

          <div className="login-footer">
            <p><a href="/register-customer">Register as Customer</a></p>
          </div>
        </div>

        <div className="login-info">
          <div className="info-item">
            <div className="info-icon">📅</div>
            <h4>Easy Booking</h4>
            <p>Reserve equipment quickly and easily</p>
          </div>
          <div className="info-item">
            <div className="info-icon">💼</div>
            <h4>Manage Fleet</h4>
            <p>Complete control over your equipment</p>
          </div>
          <div className="info-item">
            <div className="info-icon">📊</div>
            <h4>Track Analytics</h4>
            <p>Monitor bookings and revenue in real-time</p>
          </div>
        </div>
      </div>
    </div>
  );
}
