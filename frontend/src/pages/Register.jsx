import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';

export default function Register() {
  const [fullName, setFullName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [customerType, setCustomerType] = useState('ROUTINE');
  const [message, setMessage] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (event) => {
    event.preventDefault();
    setMessage('');
    try {
      await api.post('/api/auth/register', { fullName, email, password, customerType });
      setMessage('Registration successful. Please login.');
      setTimeout(() => navigate('/login'), 1200);
    } catch (error) {
      setMessage('Registration failed. Email may already exist.');
    }
  };

  return (
    <div className="container">
      <div className="card">
        <h1>Register</h1>
        {message && <div style={{ marginBottom: 16 }}>{message}</div>}
        <form onSubmit={handleSubmit}>
          <input
            className="input-field"
            value={fullName}
            onChange={(e) => setFullName(e.target.value)}
            placeholder="Full name"
            required
          />
          <input
            className="input-field"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="Email"
            required
          />
          <input
            className="input-field"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="Password"
            required
          />
          <select className="input-field" value={customerType} onChange={(e) => setCustomerType(e.target.value)}>
            <option value="ROUTINE">Routine</option>
            <option value="ACCOUNT_PRIORITY">Account Priority</option>
            <option value="HDB">HDB</option>
            <option value="NORMAL">Normal</option>
          </select>
          <button className="button" type="submit">Register</button>
        </form>
      </div>
    </div>
  );
}
