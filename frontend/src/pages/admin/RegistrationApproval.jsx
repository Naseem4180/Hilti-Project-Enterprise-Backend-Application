import { useState, useEffect } from 'react';
import api from '../../services/api';
import './RegistrationApproval.css';

export default function RegistrationApproval() {
  const [activeTab, setActiveTab] = useState('pending');
  const [registrations, setRegistrations] = useState([]);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');
  const [rejectionReason, setRejectionReason] = useState('');
  const [selectedRegistrationId, setSelectedRegistrationId] = useState(null);

  useEffect(() => {
    fetchRegistrations();
  }, [activeTab]);

  const fetchRegistrations = async () => {
    setLoading(true);
    try {
      const endpoint = `/api/admin/registrations/${activeTab}`;
      const response = await api.get(endpoint);
      setRegistrations(response.data || []);
      setMessage('');
    } catch (error) {
      setMessage('Failed to fetch registrations');
      setRegistrations([]);
    } finally {
      setLoading(false);
    }
  };

  const handleApprove = async (id) => {
    if (window.confirm('Are you sure you want to approve this registration?')) {
      try {
        const response = await api.post(`/api/admin/registrations/${id}/approve`);
        setMessage('Registration approved successfully!');
        setTimeout(() => {
          fetchRegistrations();
          setMessage('');
        }, 1000);
      } catch (error) {
        setMessage('Failed to approve registration');
      }
    }
  };

  const handleReject = async (id) => {
    if (!rejectionReason.trim()) {
      alert('Please enter a rejection reason');
      return;
    }
    if (window.confirm('Are you sure you want to reject this registration?')) {
      try {
        const params = new URLSearchParams({ reason: rejectionReason });
        await api.post(`/api/admin/registrations/${id}/reject?${params}`);
        setMessage('Registration rejected successfully!');
        setRejectionReason('');
        setSelectedRegistrationId(null);
        setTimeout(() => {
          fetchRegistrations();
          setMessage('');
        }, 1000);
      } catch (error) {
        setMessage('Failed to reject registration');
      }
    }
  };

  return (
    <div className="approval-container">
      <div className="approval-header">
        <h1>Customer Registration Approvals</h1>
        <p>Manage customer registration requests</p>
      </div>

      <div className="tabs">
        <button
          className={`tab-button ${activeTab === 'pending' ? 'active' : ''}`}
          onClick={() => setActiveTab('pending')}
        >
          Pending ({registrations.length})
        </button>
        <button
          className={`tab-button ${activeTab === 'approved' ? 'active' : ''}`}
          onClick={() => setActiveTab('approved')}
        >
          Approved ({registrations.length})
        </button>
        <button
          className={`tab-button ${activeTab === 'rejected' ? 'active' : ''}`}
          onClick={() => setActiveTab('rejected')}
        >
          Rejected ({registrations.length})
        </button>
      </div>

      {message && (
        <div className={`message ${message.includes('success') ? 'message-success' : 'message-error'}`}>
          {message}
        </div>
      )}

      {loading ? (
        <div className="loading">Loading registrations...</div>
      ) : registrations.length === 0 ? (
        <div className="no-data">No registrations found</div>
      ) : (
        <div className="registrations-list">
          {registrations.map(registration => (
            <div key={registration.id} className="registration-card">
              <div className="card-header">
                <div>
                  <h3>{registration.firstName} {registration.lastName}</h3>
                  <p className="company">{registration.companyName}</p>
                </div>
                <div className="status-badge">
                  {registration.status}
                </div>
              </div>

              <div className="card-body">
                <div className="info-row">
                  <span className="label">Email:</span>
                  <span className="value">{registration.email}</span>
                </div>
                <div className="info-row">
                  <span className="label">Account Number:</span>
                  <span className="value">{registration.accountNumber}</span>
                </div>
                <div className="info-row">
                  <span className="label">Contact Number:</span>
                  <span className="value">{registration.contactNumber}</span>
                </div>
                <div className="info-row">
                  <span className="label">Submitted:</span>
                  <span className="value">{new Date(registration.createdAt).toLocaleDateString()}</span>
                </div>

                {registration.rejectionReason && (
                  <div className="info-row rejection-reason">
                    <span className="label">Rejection Reason:</span>
                    <span className="value">{registration.rejectionReason}</span>
                  </div>
                )}

                {registration.approvedBy && (
                  <div className="info-row">
                    <span className="label">Approved By:</span>
                    <span className="value">{registration.approvedBy}</span>
                  </div>
                )}
              </div>

              {activeTab === 'pending' && (
                <div className="card-actions">
                  <button
                    className="btn btn-approve"
                    onClick={() => handleApprove(registration.id)}
                  >
                    Approve
                  </button>
                  <button
                    className="btn btn-reject"
                    onClick={() => setSelectedRegistrationId(
                      selectedRegistrationId === registration.id ? null : registration.id
                    )}
                  >
                    Reject
                  </button>
                </div>
              )}

              {selectedRegistrationId === registration.id && activeTab === 'pending' && (
                <div className="rejection-form">
                  <textarea
                    className="rejection-textarea"
                    placeholder="Enter rejection reason..."
                    value={rejectionReason}
                    onChange={(e) => setRejectionReason(e.target.value)}
                  />
                  <div className="rejection-actions">
                    <button
                      className="btn btn-small btn-confirm"
                      onClick={() => handleReject(registration.id)}
                    >
                      Confirm Rejection
                    </button>
                    <button
                      className="btn btn-small btn-cancel"
                      onClick={() => {
                        setSelectedRegistrationId(null);
                        setRejectionReason('');
                      }}
                    >
                      Cancel
                    </button>
                  </div>
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
