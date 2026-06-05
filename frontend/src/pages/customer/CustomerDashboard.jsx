import React, { useState } from 'react';
import Sidebar from '../../components/Sidebar';
import Header from '../../components/Header';
import '../../styles/CustomerDashboard.css';

export default function CustomerDashboard() {
  const [sidebarOpen, setSidebarOpen] = useState(true);
  const [activeTab, setActiveTab] = useState('dashboard');
  
  // Initialized to 0 to remove random numbers
  const [bookingStats] = useState({
    totalBookings: 0,
    completedBookings: 0,
    cancelledBookings: 0,
  });

  // Emptied the array to remove random table data
  const [bookings] = useState([]);

  const toggleSidebar = () => {
    setSidebarOpen(!sidebarOpen);
  };

  const getStatusColor = (status) => {
    switch(status) {
      case 'Completed': return '#10B981';
      case 'Active': return '#3B82F6';
      case 'Cancelled': return '#EF4444';
      case 'Pending': return '#F59E0B';
      default: return '#6B7280';
    }
  };

  return (
    <div className="dashboard-wrapper">
      <Sidebar isOpen={sidebarOpen} toggleSidebar={toggleSidebar} />
      <div className="dashboard-container">
        <Header toggleSidebar={toggleSidebar} />
        
        <main className="dashboard-main">
          {/* Tab Navigation */}
          <div className="tab-navigation">
            <button 
              className={`tab-btn ${activeTab === 'dashboard' ? 'active' : ''}`}
              onClick={() => setActiveTab('dashboard')}
            >
              📊 Dashboard
            </button>
            <button 
              className={`tab-btn ${activeTab === 'bookings' ? 'active' : ''}`}
              onClick={() => setActiveTab('bookings')}
            >
              📅 My Bookings
            </button>
            <button 
              className={`tab-btn ${activeTab === 'profile' ? 'active' : ''}`}
              onClick={() => setActiveTab('profile')}
            >
              👤 Profile
            </button>
          </div>

          {/* Dashboard Tab Content */}
          {activeTab === 'dashboard' && (
            <div className="stats-grid customer">
              <div className="stat-card primary">
                <div className="stat-icon">📅</div>
                <div className="stat-content">
                  <div className="stat-label">Total Bookings</div>
                  <div className="stat-value">{bookingStats.totalBookings}</div>
                </div>
              </div>
              <div className="stat-card success">
                <div className="stat-icon">✅</div>
                <div className="stat-content">
                  <div className="stat-label">Completed Bookings</div>
                  <div className="stat-value">{bookingStats.completedBookings}</div>
                </div>
              </div>
              <div className="stat-card error" style={{ borderLeft: '4px solid #EF4444' }}>
                <div className="stat-icon">❌</div>
                <div className="stat-content">
                  <div className="stat-label">Cancelled Bookings</div>
                  <div className="stat-value">{bookingStats.cancelledBookings}</div>
                </div>
              </div>
            </div>
          )}

          {/* Bookings Tab Content */}
          {activeTab === 'bookings' && (
            <div className="table-section">
              <div className="section-header">
                <h3>📅 My Bookings</h3>
                <button className="btn-primary">+ New Booking</button>
              </div>
              <table className="data-table">
                <thead>
                  <tr>
                    <th>Booking ID</th>
                    <th>Equipment</th>
                    <th>Start Date</th>
                    <th>End Date</th>
                    <th>Status</th>
                    <th>Cost</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {bookings.length > 0 ? (
                    bookings.map((booking) => (
                      <tr key={booking.id}>
                        <td className="booking-id">{booking.id}</td>
                        <td>{booking.equipment}</td>
                        <td>{booking.startDate}</td>
                        <td>{booking.endDate}</td>
                        <td>
                          <span className="status-badge" style={{ backgroundColor: getStatusColor(booking.status) }}>
                            {booking.status}
                          </span>
                        </td>
                        <td className="amount">{booking.cost}</td>
                        <td>
                          <div className="action-buttons">
                            <button className="btn-sm edit">✏️</button>
                            <button className="btn-sm view">👁️</button>
                          </div>
                        </td>
                      </tr>
                    ))
                  ) : (
                    <tr>
                      <td colSpan="7" style={{ textAlign: 'center', padding: '20px' }}>
                        No bookings found.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          )}

          {/* Profile Tab Content */}
          {activeTab === 'profile' && (
            <div className="profile-section">
              <div className="profile-card">
                <h3>👤 Profile Information</h3>
                <div className="profile-form">
                  <div className="form-group">
                    <label>Full Name</label>
                    <input type="text" defaultValue="" placeholder="Enter full name" disabled />
                  </div>
                  <div className="form-group">
                    <label>Email</label>
                    <input type="email" defaultValue="" placeholder="Enter email" disabled />
                  </div>
                  <div className="form-group">
                    <label>Phone</label>
                    <input type="tel" defaultValue="" placeholder="Enter phone number" disabled />
                  </div>
                  <div className="form-group">
                    <label>Company</label>
                    <input type="text" defaultValue="" placeholder="Enter company name" disabled />
                  </div>
                  <button className="btn-primary">Edit Profile</button>
                </div>
              </div>

              <div className="profile-card">
                <h3>🔐 Security Settings</h3>
                <div className="profile-form">
                  <button className="btn-secondary">🔑 Change Password</button>
                  <button className="btn-secondary">📱 Two-Factor Authentication</button>
                  <button className="btn-secondary">🚪 Active Sessions</button>
                </div>
              </div>
            </div>
          )}
        </main>
      </div>
    </div>
  );
}