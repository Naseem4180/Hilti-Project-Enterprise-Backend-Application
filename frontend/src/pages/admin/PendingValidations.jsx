import { useState, useEffect } from 'react';
import api from '../../services/api';
import './PendingValidations.css';

export default function PendingValidations() {
  const [validations, setValidations] = useState([]);
  const [filteredValidations, setFilteredValidations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(10);
  const [searchText, setSearchText] = useState('');
  const [expandedRow, setExpandedRow] = useState(null);
  const [selectedRows, setSelectedRows] = useState(new Set());

  // Filter states
  const [filters, setFilters] = useState({
    firstName: '',
    lastName: '',
    sapAccountId: '',
    contactNumber: '',
    companyName: ''
  });

  useEffect(() => {
    fetchValidations();
    const interval = setInterval(fetchValidations, 30000); // Refresh every 30 seconds
    return () => clearInterval(interval);
  }, []);

  const fetchValidations = async () => {
    try {
      setLoading(true);
      const response = await api.get('/api/customer-registrations');
      setValidations(response.data || []);
      setFilteredValidations(response.data || []);
    } catch (error) {
      console.error('Error fetching validations:', error);
    } finally {
      setLoading(false);
    }
  };

  // Global search
  useEffect(() => {
    let filtered = validations;

    if (searchText) {
      const query = searchText.toLowerCase();
      filtered = filtered.filter(v => 
        v.firstName?.toLowerCase().includes(query) ||
        v.lastName?.toLowerCase().includes(query) ||
        v.email?.toLowerCase().includes(query) ||
        v.accountNumber?.toLowerCase().includes(query) ||
        v.companyName?.toLowerCase().includes(query)
      );
    }

    if (filters.firstName) {
      filtered = filtered.filter(v => v.firstName?.toLowerCase().includes(filters.firstName.toLowerCase()));
    }
    if (filters.lastName) {
      filtered = filtered.filter(v => v.lastName?.toLowerCase().includes(filters.lastName.toLowerCase()));
    }
    if (filters.sapAccountId) {
      filtered = filtered.filter(v => v.accountNumber?.toLowerCase().includes(filters.sapAccountId.toLowerCase()));
    }
    if (filters.contactNumber) {
      filtered = filtered.filter(v => v.contactNumber?.includes(filters.contactNumber));
    }
    if (filters.companyName) {
      filtered = filtered.filter(v => v.companyName?.toLowerCase().includes(filters.companyName.toLowerCase()));
    }

    setFilteredValidations(filtered);
    setCurrentPage(1);
  }, [filters, validations, searchText]);

  const handleFilterChange = (field, value) => {
    setFilters(prev => ({ ...prev, [field]: value }));
  };

  const clearFilters = () => {
    setFilters({
      firstName: '',
      lastName: '',
      sapAccountId: '',
      contactNumber: '',
      companyName: ''
    });
    setSearchText('');
  };

  // Pagination
  const totalPages = Math.ceil(filteredValidations.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const paginatedValidations = filteredValidations.slice(startIndex, startIndex + itemsPerPage);

  const handleApprove = async (id) => {
    try {
      await api.put(`/api/customer-registrations/${id}/approve`);
      fetchValidations();
    } catch (error) {
      console.error('Error approving registration:', error);
    }
  };

  const handleReject = async (id) => {
    try {
      const reason = prompt('Enter rejection reason:');
      if (reason) {
        await api.put(`/api/customer-registrations/${id}/reject`, { rejectionReason: reason });
        fetchValidations();
      }
    } catch (error) {
      console.error('Error rejecting registration:', error);
    }
  };

  const toggleRowSelection = (id) => {
    const newSelected = new Set(selectedRows);
    if (newSelected.has(id)) {
      newSelected.delete(id);
    } else {
      newSelected.add(id);
    }
    setSelectedRows(newSelected);
  };

  const toggleAllSelection = () => {
    if (selectedRows.size === paginatedValidations.length) {
      setSelectedRows(new Set());
    } else {
      const newSelected = new Set(paginatedValidations.map(v => v.id));
      setSelectedRows(newSelected);
    }
  };

  const pendingCount = validations.filter(v => v.status === 'PENDING').length;
  const approvedCount = validations.filter(v => v.status === 'APPROVED').length;
  const rejectedCount = validations.filter(v => v.status === 'REJECTED').length;

  return (
    <div className="pending-validations">
      {/* Header Section */}
      <div className="section-header">
        <div>
          <h1>User Validation</h1>
          <p className="subtitle">Manage and approve customer registrations</p>
        </div>
        <div className="header-stats">
          <div className="stat-card">
            <div className="stat-number">{pendingCount}</div>
            <div className="stat-label">Pending</div>
          </div>
          <div className="stat-card approved">
            <div className="stat-number">{approvedCount}</div>
            <div className="stat-label">Approved</div>
          </div>
          <div className="stat-card rejected">
            <div className="stat-number">{rejectedCount}</div>
            <div className="stat-label">Rejected</div>
          </div>
        </div>
      </div>

      {/* Global Search */}
      <div className="search-section">
        <div className="search-input-wrapper">
          <input 
            type="text" 
            placeholder="Search by name, email, account number..." 
            value={searchText}
            onChange={(e) => setSearchText(e.target.value)}
            className="global-search"
          />
          <span className="search-icon">🔍</span>
        </div>
        <button className="refresh-btn" onClick={fetchValidations}>
          🔄 Refresh
        </button>
      </div>

      {/* Filters */}
      <div className="filters-section">
        <div className="filters-grid">
          <div className="filter-group">
            <label>First Name</label>
            <input 
              type="text" 
              placeholder="First name..." 
              value={filters.firstName}
              onChange={(e) => handleFilterChange('firstName', e.target.value)}
            />
          </div>

          <div className="filter-group">
            <label>Last Name</label>
            <input 
              type="text" 
              placeholder="Last name..." 
              value={filters.lastName}
              onChange={(e) => handleFilterChange('lastName', e.target.value)}
            />
          </div>

          <div className="filter-group">
            <label>SAP Account</label>
            <input 
              type="text" 
              placeholder="Account ID..." 
              value={filters.sapAccountId}
              onChange={(e) => handleFilterChange('sapAccountId', e.target.value)}
            />
          </div>

          <div className="filter-group">
            <label>Contact</label>
            <input 
              type="text" 
              placeholder="Phone number..." 
              value={filters.contactNumber}
              onChange={(e) => handleFilterChange('contactNumber', e.target.value)}
            />
          </div>

          <div className="filter-group">
            <label>Company</label>
            <input 
              type="text" 
              placeholder="Company name..." 
              value={filters.companyName}
              onChange={(e) => handleFilterChange('companyName', e.target.value)}
            />
          </div>
        </div>

        <button className="clear-filters-btn" onClick={clearFilters}>Clear</button>
      </div>

      {/* Loading State */}
      {loading && (
        <div className="loading-state">
          <div className="spinner"></div>
          <p>Loading validations...</p>
        </div>
      )}

      {/* Empty State */}
      {!loading && filteredValidations.length === 0 && (
        <div className="empty-state">
          <div className="empty-icon">📭</div>
          <h3>No validations found</h3>
          <p>Try adjusting your search or filters</p>
        </div>
      )}

      {/* Table */}
      {!loading && filteredValidations.length > 0 && (
        <div className="table-container">
          <div className="table-wrapper">
            <table className="data-table">
              <thead>
                <tr>
                  <th className="checkbox-col">
                    <input 
                      type="checkbox" 
                      checked={selectedRows.size === paginatedValidations.length && paginatedValidations.length > 0}
                      onChange={toggleAllSelection}
                    />
                  </th>
                  <th>Name</th>
                  <th>Email</th>
                  <th>Account</th>
                  <th>Company</th>
                  <th>Phone</th>
                  <th>Status</th>
                  <th className="actions-col">Actions</th>
                </tr>
              </thead>
              <tbody>
                {paginatedValidations.map(validation => (
                  <tr key={validation.id} className={`${validation.status?.toLowerCase()} ${expandedRow === validation.id ? 'expanded' : ''}`}>
                    <td className="checkbox-col">
                      <input 
                        type="checkbox" 
                        checked={selectedRows.has(validation.id)}
                        onChange={() => toggleRowSelection(validation.id)}
                      />
                    </td>
                    <td className="name-cell">
                      <div className="name-content">
                        <div className="avatar">{validation.firstName?.charAt(0)}{validation.lastName?.charAt(0)}</div>
                        <div className="name-text">
                          <strong>{validation.firstName} {validation.lastName}</strong>
                        </div>
                      </div>
                    </td>
                    <td>{validation.email}</td>
                    <td className="account-cell">{validation.accountNumber}</td>
                    <td>{validation.companyName}</td>
                    <td>{validation.contactNumber}</td>
                    <td>
                      <span className={`status-badge ${validation.status?.toLowerCase()}`}>
                        {validation.status === 'PENDING' && '⏳'}
                        {validation.status === 'APPROVED' && '✅'}
                        {validation.status === 'REJECTED' && '❌'}
                        {' '}{validation.status}
                      </span>
                    </td>
                    <td className="actions-cell">
                      {validation.status === 'PENDING' && (
                        <div className="action-buttons">
                          <button 
                            className="btn-approve"
                            onClick={() => handleApprove(validation.id)}
                            title="Approve"
                          >
                            ✓
                          </button>
                          <button 
                            className="btn-reject"
                            onClick={() => handleReject(validation.id)}
                            title="Reject"
                          >
                            ✕
                          </button>
                        </div>
                      )}
                      {validation.status !== 'PENDING' && (
                        <span className="status-locked">Locked</span>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {/* Pagination */}
          <div className="pagination">
            <div className="pagination-info">
              Showing {startIndex + 1} to {Math.min(startIndex + itemsPerPage, filteredValidations.length)} of {filteredValidations.length}
            </div>

            <div className="pagination-controls">
              <select value={itemsPerPage} onChange={(e) => setItemsPerPage(Number(e.target.value))}>
                <option value={5}>5 per page</option>
                <option value={10}>10 per page</option>
                <option value={25}>25 per page</option>
                <option value={50}>50 per page</option>
              </select>

              <div className="pagination-buttons">
                <button 
                  onClick={() => setCurrentPage(Math.max(1, currentPage - 1))}
                  disabled={currentPage === 1}
                  className="nav-btn"
                >
                  ← Prev
                </button>

                {Array.from({ length: Math.min(5, totalPages) }, (_, i) => {
                  let page;
                  if (totalPages <= 5) {
                    page = i + 1;
                  } else if (currentPage <= 3) {
                    page = i + 1;
                  } else if (currentPage >= totalPages - 2) {
                    page = totalPages - 4 + i;
                  } else {
                    page = currentPage - 2 + i;
                  }
                  return (
                    <button
                      key={page}
                      className={`page-btn ${currentPage === page ? 'active' : ''}`}
                      onClick={() => setCurrentPage(page)}
                    >
                      {page}
                    </button>
                  );
                })}

                <button 
                  onClick={() => setCurrentPage(Math.min(totalPages, currentPage + 1))}
                  disabled={currentPage === totalPages}
                  className="nav-btn"
                >
                  Next →
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
