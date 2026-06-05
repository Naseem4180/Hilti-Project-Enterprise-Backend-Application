import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import '../styles/Header.css';

export default function Header({ toggleSidebar }) {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [showUserMenu, setShowUserMenu] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');

  const currentTime = new Date().toLocaleTimeString('en-US', { 
    hour: '2-digit', 
    minute: '2-digit'
  });

  const currentDate = new Date().toLocaleDateString('en-US', { 
    month: 'short', 
    day: 'numeric',
    year: 'numeric'
  });

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <header className="header">
      <div className="header-left">
        <button className="sidebar-toggle-btn" onClick={toggleSidebar} title="Toggle Sidebar">
          ≡
        </button>
        <div className="header-info">
          <span className="current-date">📅 {currentDate}</span>
          <span className="current-time">⏱️ {currentTime}</span>
        </div>
      </div>

      <div className="header-center">
        <div className="search-box">
          <input 
            type="text" 
            placeholder="Search bookings, customers..." 
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
          <span className="search-icon">🔍</span>
        </div>
      </div>

      <div className="header-right">
        <button className="notification-btn" title="Notifications">
          🔔
          <span className="notification-badge">3</span>
        </button>

        <div className="user-menu-wrapper">
          <button 
            className="user-button"
            onClick={() => setShowUserMenu(!showUserMenu)}
          >
            <div className="user-avatar-small">
              {user?.email?.charAt(0).toUpperCase()}
            </div>
            <div className="user-info-small">
              <div className="user-name">
                {user?.email?.split('@')[0]}
              </div>
              <div className="user-role-small">
                {user?.role?.replace('ROLE_', '')}
              </div>
            </div>
            <span className="dropdown-arrow">▼</span>
          </button>

          {showUserMenu && (
            <div className="user-dropdown">
              <div className="dropdown-header">
                <div className="dropdown-avatar">
                  {user?.email?.charAt(0).toUpperCase()}
                </div>
                <div>
                  <div className="dropdown-name">{user?.email}</div>
                  <div className="dropdown-role">
                    {user?.role?.replace('ROLE_', '')}
                  </div>
                </div>
              </div>
              <div className="dropdown-divider"></div>
              <button className="dropdown-item">👤 Profile</button>
              <button className="dropdown-item">⚙️ Settings</button>
              <button className="dropdown-item">🔐 Change Password</button>
              <div className="dropdown-divider"></div>
              <button className="dropdown-item logout" onClick={handleLogout}>
                🚪 Sign Out
              </button>
            </div>
          )}
        </div>
      </div>
    </header>
  );
}
