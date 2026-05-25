import { useAuth } from '../../context/AuthContext';

export default function ManagerDashboard() {
  const { logout, user } = useAuth();

  return (
    <div className="container">
      <div className="navbar">
        <h1>Manager Dashboard</h1>
        <button className="button" onClick={logout}>Logout</button>
      </div>
      <div className="card">
        <p>Welcome {user?.email}. Use this dashboard to review reports and manage users.</p>
      </div>
    </div>
  );
}
