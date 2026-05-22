import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError('');
    try {
      const user = await login(email, password);
      if (user.role === 'ROLE_CUSTOMER') navigate('/customer');
      if (user.role === 'ROLE_ADMIN') navigate('/admin');
      if (user.role === 'ROLE_FE') navigate('/fe');
      if (user.role === 'ROLE_MANAGER') navigate('/admin');
    } catch (err) {
      setError('Email or password incorrect.');
    }
  };

  return (
    <div className="container">
      <div className="card">
        <h1>Hilti Booking Login</h1>
        <p>Use admin@hilti.com / Admin123! or customer@hilti.com / Customer123!</p>
        {error && <div style={{ color: '#b91c1c' }}>{error}</div>}
        <form onSubmit={handleSubmit}>
          <input
            className="input-field"
            type="email"
            placeholder="Email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
          <input
            className="input-field"
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          <button className="button" type="submit">Login</button>
        </form>
        <p style={{ marginTop: 16 }}>
          New to Hilti? <Link to="/register">Register</Link>
        </p>
      </div>
    </div>
  );
}
