import { useState, useEffect } from 'react';
import api from '../../services/api';
import { BarChart, Bar, PieChart, Pie, Cell, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import './BookingInProgress.css';

export default function BookingInProgress() {
  const [bookings, setBookings] = useState([]);
  const [filteredBookings, setFilteredBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(50);

  // Filter states
  const [filters, setFilters] = useState({
    postalCode: '',
    createdAt: '',
    companyName: '',
    slotConfirmedDate: '',
    projectSelection: '',
    category: '',
    testers: '',
    bookingStatus: '',
    soNumber: '',
    bookingNumber: ''
  });

  useEffect(() => {
    fetchBookings();
  }, []);

  const fetchBookings = async () => {
    try {
      setLoading(true);
      const response = await api.get('/api/bookings');
      setBookings(response.data || []);
      setFilteredBookings(response.data || []);
    } catch (error) {
      console.error('Error fetching bookings:', error);
    } finally {
      setLoading(false);
    }
  };

  // Apply filters
  useEffect(() => {
    let filtered = bookings;

    if (filters.bookingNumber) {
      filtered = filtered.filter(b => b.bookingNumber?.toLowerCase().includes(filters.bookingNumber.toLowerCase()));
    }
    if (filters.soNumber) {
      filtered = filtered.filter(b => b.soNumber?.toLowerCase().includes(filters.soNumber.toLowerCase()));
    }
    if (filters.companyName) {
      filtered = filtered.filter(b => b.companyName?.toLowerCase().includes(filters.companyName.toLowerCase()));
    }
    if (filters.postalCode) {
      filtered = filtered.filter(b => b.postalCode?.includes(filters.postalCode));
    }
    if (filters.bookingStatus) {
      filtered = filtered.filter(b => b.status === filters.bookingStatus);
    }

    setFilteredBookings(filtered);
    setCurrentPage(1);
  }, [filters, bookings]);

  const handleFilterChange = (field, value) => {
    setFilters(prev => ({ ...prev, [field]: value }));
  };

  const clearFilters = () => {
    setFilters({
      postalCode: '',
      createdAt: '',
      companyName: '',
      slotConfirmedDate: '',
      projectSelection: '',
      category: '',
      testers: '',
      bookingStatus: '',
      soNumber: '',
      bookingNumber: ''
    });
  };

  // Charts data
  const companyData = bookings.reduce((acc, b) => {
    const existing = acc.find(x => x.name === b.companyName);
    if (existing) {
      existing.bookings += 1;
    } else {
      acc.push({ name: b.companyName || 'Unknown', bookings: 1 });
    }
    return acc;
  }, []).sort((a, b) => b.bookings - a.bookings).slice(0, 10);

  const statusData = [
    { name: 'Confirmed', value: bookings.filter(b => b.status === 'CONFIRMED').length },
    { name: 'In Progress', value: bookings.filter(b => b.status === 'IN_PROGRESS').length },
    { name: 'Completed', value: bookings.filter(b => b.status === 'COMPLETED').length },
    { name: 'Cancelled', value: bookings.filter(b => b.status === 'CANCELLED').length },
    { name: 'Postponed', value: bookings.filter(b => b.status === 'POSTPONED').length }
  ].filter(d => d.value > 0);

  const COLORS = ['#3B82F6', '#10B981', '#F59E0B', '#EF4444', '#8B5CF6'];

  // Pagination
  const totalPages = Math.ceil(filteredBookings.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const paginatedBookings = filteredBookings.slice(startIndex, startIndex + itemsPerPage);

  return (
    <div className="booking-in-progress">
      <div className="content-header">
        <h2>Booking In progress</h2>
      </div>

      {/* Filters */}
      <div className="filters-section">
        <div className="filters-grid">
          <div className="filter-group">
            <label>Postal Code</label>
            <select value={filters.postalCode} onChange={(e) => handleFilterChange('postalCode', e.target.value)}>
              <option value="">All</option>
              {[...new Set(bookings.map(b => b.postalCode))].filter(Boolean).map(pc => (
                <option key={pc} value={pc}>{pc}</option>
              ))}
            </select>
          </div>

          <div className="filter-group">
            <label>Created At</label>
            <input type="date" value={filters.createdAt} onChange={(e) => handleFilterChange('createdAt', e.target.value)} />
          </div>

          <div className="filter-group">
            <label>Company Name</label>
            <input 
              type="text" 
              placeholder="Search company..." 
              value={filters.companyName}
              onChange={(e) => handleFilterChange('companyName', e.target.value)}
            />
          </div>

          <div className="filter-group">
            <label>Slot Confirmed Date</label>
            <input type="date" value={filters.slotConfirmedDate} onChange={(e) => handleFilterChange('slotConfirmedDate', e.target.value)} />
          </div>

          <div className="filter-group">
            <label>Project Selection</label>
            <input type="text" placeholder="Project..." value={filters.projectSelection} onChange={(e) => handleFilterChange('projectSelection', e.target.value)} />
          </div>

          <div className="filter-group">
            <label>Category</label>
            <input type="text" placeholder="Category..." value={filters.category} onChange={(e) => handleFilterChange('category', e.target.value)} />
          </div>

          <div className="filter-group">
            <label>Testers</label>
            <input type="text" placeholder="Testers..." value={filters.testers} onChange={(e) => handleFilterChange('testers', e.target.value)} />
          </div>

          <div className="filter-group">
            <label>Booking Status</label>
            <select value={filters.bookingStatus} onChange={(e) => handleFilterChange('bookingStatus', e.target.value)}>
              <option value="">All</option>
              <option value="CONFIRMED">Confirmed</option>
              <option value="IN_PROGRESS">In Progress</option>
              <option value="COMPLETED">Completed</option>
              <option value="CANCELLED">Cancelled</option>
              <option value="POSTPONED">Postponed</option>
            </select>
          </div>

          <div className="filter-group">
            <label>SO Number</label>
            <input 
              type="text" 
              placeholder="Search SO..." 
              value={filters.soNumber}
              onChange={(e) => handleFilterChange('soNumber', e.target.value)}
            />
          </div>

          <div className="filter-group">
            <label>Booking Number</label>
            <input 
              type="text" 
              placeholder="Search booking..." 
              value={filters.bookingNumber}
              onChange={(e) => handleFilterChange('bookingNumber', e.target.value)}
            />
          </div>
        </div>

        <button className="clear-filters-btn" onClick={clearFilters}>Clear Filters</button>
      </div>

      {/* Charts */}
      <div className="charts-section">
        <div className="chart-container">
          <h3>Bookings by Company</h3>
          {companyData.length > 0 ? (
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={companyData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="name" angle={-45} textAnchor="end" height={100} />
                <YAxis />
                <Tooltip />
                <Bar dataKey="bookings" fill="#EF4444" />
              </BarChart>
            </ResponsiveContainer>
          ) : (
            <p className="no-data">No data available</p>
          )}
        </div>

        <div className="chart-container">
          <h3>Booking Status</h3>
          {statusData.length > 0 ? (
            <div className="pie-chart-wrapper">
              <ResponsiveContainer width="100%" height={300}>
                <PieChart>
                  <Pie
                    data={statusData}
                    cx="50%"
                    cy="50%"
                    innerRadius={60}
                    outerRadius={100}
                    paddingAngle={5}
                    dataKey="value"
                  >
                    {statusData.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                    ))}
                  </Pie>
                  <Tooltip />
                  <Legend />
                </PieChart>
              </ResponsiveContainer>
              <div className="chart-stats">
                <div className="stat">
                  <span className="stat-label">Total</span>
                  <span className="stat-value">{bookings.length}</span>
                </div>
              </div>
            </div>
          ) : (
            <p className="no-data">No data available</p>
          )}
        </div>
      </div>

      {/* Table */}
      <div className="table-section">
        <div className="table-header">
          <p className="total-shipments">Total Shipments: {filteredBookings.length} <span>Select orders for more action</span></p>
        </div>

        <div className="table-wrapper">
          <table className="data-table">
            <thead>
              <tr>
                <th><input type="checkbox" /></th>
                <th>Booking Number</th>
                <th>SO Number</th>
                <th>Company Name</th>
                <th>Postal Code</th>
                <th>Project Selection</th>
                <th>Slot Requested Date</th>
                <th>Slot Requested Time</th>
                <th>Slot Confirmed Date</th>
              </tr>
            </thead>
            <tbody>
              {paginatedBookings.length > 0 ? (
                paginatedBookings.map(booking => (
                  <tr key={booking.id}>
                    <td><input type="checkbox" /></td>
                    <td>{booking.bookingNumber}</td>
                    <td>{booking.soNumber || '-'}</td>
                    <td>{booking.companyName}</td>
                    <td>{booking.postalCode || '-'}</td>
                    <td>{booking.projectSelection || '-'}</td>
                    <td>{booking.slotDate || '-'}</td>
                    <td>{booking.slotTimeStart ? `${booking.slotTimeStart} - ${booking.slotTimeEnd}` : '-'}</td>
                    <td>{booking.slotDate || '-'}</td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="9" className="no-data-row">No bookings found</td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        {/* Pagination */}
        <div className="pagination">
          <select value={itemsPerPage} onChange={(e) => setItemsPerPage(Number(e.target.value))}>
            <option value={10}>10/Page</option>
            <option value={25}>25/Page</option>
            <option value={50}>50/Page</option>
            <option value={100}>100/Page</option>
          </select>

          <div className="pagination-buttons">
            <button 
              onClick={() => setCurrentPage(Math.max(1, currentPage - 1))}
              disabled={currentPage === 1}
            >
              ←
            </button>

            {Array.from({ length: totalPages }, (_, i) => i + 1).map(page => (
              <button
                key={page}
                className={`page-btn ${currentPage === page ? 'active' : ''}`}
                onClick={() => setCurrentPage(page)}
              >
                {page}
              </button>
            ))}

            <button 
              onClick={() => setCurrentPage(Math.min(totalPages, currentPage + 1))}
              disabled={currentPage === totalPages}
            >
              →
            </button>
          </div>

          <button className="refresh-btn">Refresh</button>
          <button className="export-btn">⬇</button>
          <button className="more-btn">⋮</button>
        </div>
      </div>
    </div>
  );
}
