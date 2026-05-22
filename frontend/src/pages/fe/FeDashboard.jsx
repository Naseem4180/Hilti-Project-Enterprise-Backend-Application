import { useEffect, useState } from 'react';
import api from '../../services/api';
import { useAuth } from '../../context/AuthContext';

export default function FeDashboard() {
  const { logout, user } = useAuth();
  const [message, setMessage] = useState('');

  useEffect(() => {
    setMessage('FE view is ready. You can manage assignments or submit leave requests.');
  }, []);

  return (
    <div className="container">
      <div className="navbar">
        <h1>Field Executive Dashboard</h1>
        <button className="button" onClick={logout}>Logout</button>
      </div>
      <div className="card">
        <p>Welcome {user?.email}. This screen is the FE work center.</p>
        <p>{message}</p>
      </div>
    </div>
  );
}
