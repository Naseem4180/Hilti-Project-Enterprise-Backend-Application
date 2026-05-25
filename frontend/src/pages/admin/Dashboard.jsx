import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import './Dashboard.css';
import PendingValidations from './PendingValidations';

export default function Dashboard() {
  const { logout, user } = useAuth();
  const navigate = useNavigate();
  const [sidebarOpen, setSidebarOpen] = useState(true);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const currentDate = new Date().toLocaleDateString('en-US', { 
    month: 'long', 
    day: 'numeric', 
    year: 'numeric' 
  });

  return (
    <div className="dashboard-container">
      {/* Sidebar */}
      <aside className={`sidebar ${sidebarOpen ? 'open' : 'closed'}`}>
        <div className="sidebar-header">
          <div className="logo">H</div>
          {sidebarOpen && (
            <div className="logo-text">
              <div className="logo-brand">Hilti</div>
              <div className="logo-subtext">Booking</div>
            </div>
          )}
        </div>

        <nav className="sidebar-nav">
          <button className="nav-item active">
            <span className="icon">📋</span>
            {sidebarOpen && <span>User Validation</span>}
          </button>

          <button className="nav-item">
            <span className="icon">👥</span>
            {sidebarOpen && <span>Customers</span>}
          </button>

          <button className="nav-item">
            <span className="icon">📊</span>
            {sidebarOpen && <span>Reports</span>}
          </button>

          <button className="nav-item">
            <span className="icon">⚙️</span>
            {sidebarOpen && <span>Settings</span>}
          </button>
        </nav>

        <div className="sidebar-footer">
          <button className="nav-item logout-item" onClick={handleLogout}>
            <span className="icon">🚪</span>
            {sidebarOpen && <span>Log Out</span>}
          </button>
        </div>
      </aside>

      {/* Main Content */}
      <main className="dashboard-main">
        {/* Header */}
        <header className="dashboard-header">
          <div className="header-left">
            <button 
              className="sidebar-toggle"
              onClick={() => setSidebarOpen(!sidebarOpen)}
            >
              ☰
            </button>
            <div className="header-date">
              📅 {currentDate}
            </div>
          </div>

          <div className="header-right">
            <div className="search-box">
              <input type="text" placeholder="Search by name or email..." />
              <span className="search-icon">🔍</span>
            </div>
            <button className="header-icon">🔔</button>
            <div className="user-menu">
              <div className="user-avatar">{user?.email?.charAt(0).toUpperCase()}</div>
            </div>
          </div>
        </header>

        {/* Content Area */}
        <div className="dashboard-content">
          <PendingValidations />
        </div>
      </main>
    </div>
  );
}
