import React, { useState, useEffect } from 'react';
import Sidebar from '../../components/Sidebar';
import Header from '../../components/Header';
import api from '../../services/api';
import '../../styles/RegistrationApproval.css';

export default function RegistrationApproval() {
  const [sidebarOpen, setSidebarOpen] = useState(true);
  const [activeTab, setActiveTab] = useState('pending');
  const [registrations, setRegistrations] = useState([]);
  const [selectedRegistration, setSelectedRegistration] = useState(null);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');
  const [messageType, setMessageType] = useState('');
  const [rejectionReason, setRejectionReason] = useState('');
  const [actionLoading, setActionLoading] = useState(false);

  useEffect(() => {
    fetchRegistrations();
  }, [activeTab]);

  const fetchRegistrations = async () => {
    setLoading(true);
    try {
      const endpoint = `/api/admin/registrations/${activeTab}`;
      const response = await api.get(endpoint);
      const data = response.data || [];
      setRegistrations(data);
      setSelectedRegistration(data.length > 0 ? data[0] : null);
      setMessage('');
    } catch (error) {
      setRegistrations([]);
      setSelectedRegistration(null);
    } finally {
      setLoading(false);
    }
  };

 const handleApprove = async () => {
  if (!selectedRegistration) return;
  if (!window.confirm('Approve this registration?')) return;

  setActionLoading(true);

  try {
    await api.post(
      `/api/admin/registrations/${selectedRegistration.id}/approve`
    );

    setMessageType('success');
    setMessage('✅ Registration approved successfully!');

    setTimeout(() => {
      fetchRegistrations();
      setMessage('');
    }, 1500);

  } catch (error) {
    console.error('Approve Error:', error);
    setMessageType('error');
    setMessage('❌ Failed to approve registration');
  } finally {
    setActionLoading(false);
  }
};

 const handleReject = async () => {
  if (!selectedRegistration) return;

  if (!rejectionReason.trim()) {
    setMessageType('error');
    setMessage('❌ Please enter a rejection reason');
    return;
  }

  if (!window.confirm('Reject this registration?')) return;

  setActionLoading(true);

  try {
    await api.post(
      `/api/admin/registrations/${selectedRegistration.id}/reject?reason=${encodeURIComponent(rejectionReason)}`
    );

    setMessageType('success');
    setMessage('✅ Registration rejected successfully!');
    setRejectionReason('');

    setTimeout(() => {
      fetchRegistrations();
      setMessage('');
    }, 1500);

  } catch (error) {
    console.error('Reject Error:', error);
    setMessageType('error');
    setMessage('❌ Failed to reject registration');
  } finally {
    setActionLoading(false);
  }
};

  const tabCounts = {
    pending: registrations.filter(r => r.status === 'PENDING').length,
    approved: registrations.filter(r => r.status === 'APPROVED').length,
    rejected: registrations.filter(r => r.status === 'REJECTED').length
  };

  const toggleSidebar = () => {
    setSidebarOpen(!sidebarOpen);
  };

  return (
    <div className="dashboard-wrapper">
      <Sidebar isOpen={sidebarOpen} toggleSidebar={toggleSidebar} />
      <div className="dashboard-container">
        <Header toggleSidebar={toggleSidebar} />

        <main className="registration-main">
          <div className="registration-layout">
            {/* Left Panel - Tabs */}
            <div className="registration-left">
              <div className="tabs-header">
                <h2>👥 Registrations</h2>
              </div>

              <div className="tabs-container">
                {[
                  { id: 'pending', label: 'Pending', icon: '⏳', count: registrations.filter(r => !r.status || r.status === 'PENDING').length },
                  { id: 'approved', label: 'Approved', icon: '✅', count: registrations.filter(r => r.status === 'APPROVED').length },
                  { id: 'rejected', label: 'Rejected', icon: '❌', count: registrations.filter(r => r.status === 'REJECTED').length }
                ].map(tab => (
                  <button
                    key={tab.id}
                    className={`registration-tab ${activeTab === tab.id ? 'active' : ''}`}
                    onClick={() => setActiveTab(tab.id)}
                  >
                    <span className="tab-icon">{tab.icon}</span>
                    <div className="tab-content">
                      <span className="tab-label">{tab.label}</span>
                      <span className="tab-count">{tab.count}</span>
                    </div>
                  </button>
                ))}
              </div>

              <div className="registrations-list">
                {loading ? (
                  <div className="loading-state">
                    <div className="spinner"></div>
                    <p>Loading...</p>
                  </div>
                ) : registrations.length === 0 ? (
                  <div className="empty-state">
                    <div className="empty-icon">📋</div>
                    <p>No registrations</p>
                  </div>
                ) : (
                  registrations.map(reg => (
                    <div
                      key={reg.id}
                      className={`registration-item ${selectedRegistration?.id === reg.id ? 'active' : ''}`}
                      onClick={() => setSelectedRegistration(reg)}
                    >
                      <div className="item-avatar">
                        {(reg.firstName?.[0] || reg.email?.[0] || 'U').toUpperCase()}
                      </div>
                      <div className="item-content">
                        <div className="item-name">{reg.firstName} {reg.lastName}</div>
                        <div className="item-company">{reg.companyName}</div>
                        <div className="item-email">{reg.email}</div>
                      </div>
                      <div className={`item-status ${reg.status?.toLowerCase() || 'pending'}`}>
                        {reg.status || 'PENDING'}
                      </div>
                    </div>
                  ))
                )}
              </div>
            </div>

            {/* Right Panel - Details */}
            <div className="registration-right">
              {message && (
                <div className={`alert alert-${messageType}`}>
                  {message}
                </div>
              )}

              {selectedRegistration ? (
                <div className="registration-details">
                  <div className="details-header">
                    <div className="header-avatar">
                      {(selectedRegistration.firstName?.[0] || selectedRegistration.email?.[0] || 'U').toUpperCase()}
                    </div>
                    <div className="header-info">
                      <h2>{selectedRegistration.firstName} {selectedRegistration.lastName}</h2>
                      <p className="company-name">{selectedRegistration.companyName}</p>
                      <div className={`status-badge ${selectedRegistration.status?.toLowerCase() || 'pending'}`}>
                        {selectedRegistration.status || 'PENDING'}
                      </div>
                    </div>
                  </div>

                  <div className="details-body">
                    <div className="details-section">
                      <h3>📋 General Information</h3>
                      <div className="info-grid">
                        <div className="info-item">
                          <span className="info-label">Email</span>
                          <span className="info-value">{selectedRegistration.email}</span>
                        </div>
                        <div className="info-item">
                          <span className="info-label">Contact Number</span>
                          <span className="info-value">{selectedRegistration.contactNumber || 'N/A'}</span>
                        </div>
                        <div className="info-item">
                          <span className="info-label">Account Number</span>
                          <span className="info-value">{selectedRegistration.accountNumber || 'N/A'}</span>
                        </div>
                        <div className="info-item">
                          <span className="info-label">Submitted Date</span>
                          <span className="info-value">{new Date(selectedRegistration.createdAt || Date.now()).toLocaleDateString()}</span>
                        </div>
                      </div>
                    </div>

                    {selectedRegistration.rejectionReason && (
                      <div className="details-section rejection-info">
                        <h3>❌ Rejection Reason</h3>
                        <p>{selectedRegistration.rejectionReason}</p>
                      </div>
                    )}

                    {(selectedRegistration.status === 'APPROVED' || selectedRegistration.approvedBy) && (
                      <div className="details-section approval-info">
                        <h3>✅ Approval Information</h3>
                        <div className="info-grid">
                          <div className="info-item">
                            <span className="info-label">Approved By</span>
                            <span className="info-value">{selectedRegistration.approvedBy || 'Admin'}</span>
                          </div>
                          <div className="info-item">
                            <span className="info-label">Approval Date</span>
                            <span className="info-value">{selectedRegistration.approvedDate ? new Date(selectedRegistration.approvedDate).toLocaleDateString() : 'N/A'}</span>
                          </div>
                        </div>
                      </div>
                    )}
                  </div>

                  {(selectedRegistration.status === 'PENDING' || !selectedRegistration.status) && (
                    <div className="details-actions">
                      <button
                        className="btn-action approve"
                        onClick={handleApprove}
                        disabled={actionLoading}
                      >
                        {actionLoading ? '⏳ Processing...' : '✅ Approve'}
                      </button>
                      <button
                        className="btn-action reject"
                        onClick={() => setSelectedRegistration({ ...selectedRegistration, showRejectForm: !selectedRegistration.showRejectForm })}
                      >
                        ❌ Reject
                      </button>
                    </div>
                  )}

                  {selectedRegistration.showRejectForm && (
                    <div className="rejection-form">
                      <textarea
                        placeholder="Enter rejection reason..."
                        value={rejectionReason}
                        onChange={(e) => setRejectionReason(e.target.value)}
                        className="rejection-textarea"
                      />
                      <div className="form-actions">
                        <button
                          className="btn-form confirm"
                          onClick={handleReject}
                          disabled={actionLoading}
                        >
                          {actionLoading ? '⏳ Processing...' : 'Confirm Rejection'}
                        </button>
                        <button
                          className="btn-form cancel"
                          onClick={() => {
                            setSelectedRegistration({ ...selectedRegistration, showRejectForm: false });
                            setRejectionReason('');
                          }}
                        >
                          Cancel
                        </button>
                      </div>
                    </div>
                  )}
                </div>
              ) : (
                <div className="no-selection">
                  <div className="no-selection-icon">👈</div>
                  <p>Select a registration from the left panel to view details</p>
                </div>
              )}
            </div>
          </div>
        </main>
      </div>
    </div>
  );
}
