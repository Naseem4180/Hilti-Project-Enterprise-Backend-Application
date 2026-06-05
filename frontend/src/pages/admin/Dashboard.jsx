import React, { useState, useEffect } from 'react';
import { BarChart, Bar, ResponsiveContainer, XAxis, YAxis, CartesianGrid, Tooltip, Legend } from 'recharts';
import Sidebar from '../../components/Sidebar';
import Header from '../../components/Header';
import api from '../../services/api';
import '../../styles/AdminDashboard.css';

const COLORS = ['#3B82F6', '#10B981', '#F59E0B', '#EF4444', '#8B5CF6'];

export default function Dashboard() {
  const [sidebarOpen, setSidebarOpen] = useState(true);
  const [activeTab, setActiveTab] = useState('overview');
  const [loading, setLoading] = useState(false);
  
  // Statistics
  const [stats, setStats] = useState({
    totalBookings: 0,
    totalCustomers: 0,
    pendingApprovals: 0,
    activeRentals: 0
  });

  // Data states
  const [bookingStats, setBookingStats] = useState([]);
  const [customers, setCustomers] = useState([]);
  const [bookings, setBookings] = useState([]);
  const [activityLogs, setActivityLogs] = useState([]);

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    setLoading(true);
    try {
      // Load statistics
      const statsResponse = await api.get('/api/admin/statistics');
      if (statsResponse.data) {
        setStats({
          totalBookings: statsResponse.data.totalBookings || 0,
          totalCustomers: statsResponse.data.totalCustomers || 0,
          pendingApprovals: statsResponse.data.pendingApprovals || 0,
          activeRentals: statsResponse.data.activeRentals || 0
        });
      }

      // Load booking statistics for chart
      const bookingStatsResponse = await api.get('/api/bookings/statistics');
      setBookingStats(bookingStatsResponse.data || []);

      // Load customers
      const customersResponse = await api.get('/api/customers');
      setCustomers(customersResponse.data || []);

      // Load bookings
      const bookingsResponse = await api.get('/api/bookings');
      setBookings(bookingsResponse.data || []);

      // Load activity logs
      const logsResponse = await api.get('/api/activity-logs');
      setActivityLogs(logsResponse.data || []);

    } catch (error) {
      console.error('Failed to load dashboard data:', error);
      // Data remains empty, no mock fallback
    } finally {
      setLoading(false);
    }
  };

  const getStatusColor = (status) => {
    if (!status) return '#6B7280';
    const statusStr = String(status).toUpperCase();
    switch(statusStr) {
      case 'APPROVED':
      case 'COMPLETED':
        return '#10B981';
      case 'PENDING':
      case 'IN_PROGRESS':
        return '#F59E0B';
      case 'REJECTED':
      case 'CANCELLED':
        return '#EF4444';
      case 'ACTIVE':
        return '#3B82F6';
      default:
        return '#6B7280';
    }
  };

  const toggleSidebar = () => {
    setSidebarOpen(!sidebarOpen);
  };

  const renderOverviewTab = () => (
    <div className="overview-content">
      {/* Statistics Cards */}
      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-header">
            <h3>📅 Total Bookings</h3>
            <span className="stat-icon">📊</span>
          </div>
          <div className="stat-value">{stats.totalBookings}</div>
          <div className="stat-footer">All-time bookings</div>
        </div>

        <div className="stat-card">
          <div className="stat-header">
            <h3>👥 Total Customers</h3>
            <span className="stat-icon">👤</span>
          </div>
          <div className="stat-value">{stats.totalCustomers}</div>
          <div className="stat-footer">Registered customers</div>
        </div>

        <div className="stat-card">
          <div className="stat-header">
            <h3>⏳ Pending Approvals</h3>
            <span className="stat-icon">⌛</span>
          </div>
          <div className="stat-value">{stats.pendingApprovals}</div>
          <div className="stat-footer">Awaiting review</div>
        </div>

        <div className="stat-card">
          <div className="stat-header">
            <h3>🚀 Active Rentals</h3>
            <span className="stat-icon">📦</span>
          </div>
          <div className="stat-value">{stats.activeRentals}</div>
          <div className="stat-footer">Currently active</div>
        </div>
      </div>

      {/* Charts */}
      {bookingStats.length > 0 && (
        <div className="charts-section">
          <div className="chart-container">
            <h3>📈 Booking Distribution</h3>
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={bookingStats}>
                <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
                <XAxis dataKey="name" stroke="#6b7280" />
                <YAxis stroke="#6b7280" />
                <Tooltip 
                  contentStyle={{ 
                    backgroundColor: '#fff', 
                    border: '1px solid #e5e7eb',
                    borderRadius: '8px'
                  }}
                />
                <Legend />
                <Bar dataKey="count" fill="#667eea" radius={[8, 8, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>
      )}
    </div>
  );

  const renderCustomersTab = () => (
    <div className="content-section">
      <h2>👥 Customers</h2>
      {loading ? (
        <div className="loading-state">Loading customers...</div>
      ) : customers.length === 0 ? (
        <div className="empty-state">No customers found</div>
      ) : (
        <div className="table-container">
          <table className="data-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Email</th>
                <th>Phone</th>
                <th>Status</th>
                <th>Join Date</th>
              </tr>
            </thead>
            <tbody>
              {customers.slice(0, 10).map((customer, idx) => (
                <tr key={customer.id || idx}>
                  <td className="cell-name">{customer.firstName} {customer.lastName}</td>
                  <td>{customer.email}</td>
                  <td>{customer.phone || 'N/A'}</td>
                  <td>
                    <span 
                      className="status-badge"
                      style={{ backgroundColor: getStatusColor(customer.status) + '20', color: getStatusColor(customer.status) }}
                    >
                      {customer.status || 'Approved'}
                    </span>
                  </td>
                  <td>{customer.createdAt ? new Date(customer.createdAt).toLocaleDateString() : 'N/A'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );

  const renderBookingsTab = () => (
    <div className="content-section">
      <h2>📅 Bookings</h2>
      {loading ? (
        <div className="loading-state">Loading bookings...</div>
      ) : bookings.length === 0 ? (
        <div className="empty-state">No bookings found</div>
      ) : (
        <div className="table-container">
          <table className="data-table">
            <thead>
              <tr>
                <th>Booking ID</th>
                <th>Customer</th>
                <th>Equipment</th>
                <th>Date</th>
                <th>Duration</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {bookings.slice(0, 10).map((booking, idx) => (
                <tr key={booking.id || idx}>
                  <td className="cell-id">{booking.id}</td>
                  <td>{booking.customerName || 'N/A'}</td>
                  <td>{booking.equipmentName || booking.equipmentType || 'N/A'}</td>
                  <td>{booking.bookingDate ? new Date(booking.bookingDate).toLocaleDateString() : 'N/A'}</td>
                  <td>{booking.duration || 'N/A'}</td>
                  <td>
                    <span 
                      className="status-badge"
                      style={{ backgroundColor: getStatusColor(booking.status) + '20', color: getStatusColor(booking.status) }}
                    >
                      {booking.status || 'Pending'}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );

  const renderLogsTab = () => (
    <div className="content-section">
      <h2>📋 Activity Logs</h2>
      {loading ? (
        <div className="loading-state">Loading activity logs...</div>
      ) : activityLogs.length === 0 ? (
        <div className="empty-state">No activity logs found</div>
      ) : (
        <div className="logs-container">
          {activityLogs.slice(0, 15).map((log, idx) => (
            <div key={log.id || idx} className="log-entry">
              <div className="log-icon">
                {log.type === 'approval' && '✅'}
                {log.type === 'booking' && '📅'}
                {log.type === 'update' && '🔄'}
                {log.type === 'registration' && '📝'}
                {log.type === 'rejection' && '❌'}
                {!log.type && '📌'}
              </div>
              <div className="log-content">
                <div className="log-action">{log.action || 'System activity'}</div>
                <div className="log-details">{log.user || 'System'} • {log.timestamp || 'Just now'}</div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );

  return (
    <div className="dashboard-wrapper">
      <Sidebar isOpen={sidebarOpen} toggleSidebar={toggleSidebar} />
      <div className="dashboard-container">
        <Header toggleSidebar={toggleSidebar} />
        
        <main className="dashboard-main">
          {/* Tab Navigation */}
          <div className="tab-navigation">
            <button 
              className={`tab-btn ${activeTab === 'overview' ? 'active' : ''}`}
              onClick={() => setActiveTab('overview')}
            >
              📊 Overview
            </button>
            <button 
              className={`tab-btn ${activeTab === 'customers' ? 'active' : ''}`}
              onClick={() => setActiveTab('customers')}
            >
              👥 Customers
            </button>
            <button 
              className={`tab-btn ${activeTab === 'bookings' ? 'active' : ''}`}
              onClick={() => setActiveTab('bookings')}
            >
              📅 Bookings
            </button>
            <button 
              className={`tab-btn ${activeTab === 'logs' ? 'active' : ''}`}
              onClick={() => setActiveTab('logs')}
            >
              📋 Activity Logs
            </button>
          </div>

          {/* Tab Content */}
          {activeTab === 'overview' && renderOverviewTab()}
          {activeTab === 'customers' && renderCustomersTab()}
          {activeTab === 'bookings' && renderBookingsTab()}
          {activeTab === 'logs' && renderLogsTab()}
        </main>
      </div>
    </div>
  );
}

