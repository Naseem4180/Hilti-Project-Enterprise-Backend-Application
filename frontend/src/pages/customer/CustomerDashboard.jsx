import { useEffect, useState } from 'react';
import api from '../../services/api';
import { useAuth } from '../../context/AuthContext';

export default function CustomerDashboard() {
  const { logout, user } = useAuth();
  const [bookings, setBookings] = useState([]);
  const [companyName, setCompanyName] = useState('');
  const [address, setAddress] = useState('');
  const [postalCode, setPostalCode] = useState('');
  const [slotDateTimeStart, setSlotDateTimeStart] = useState('');
  const [message, setMessage] = useState('');

  useEffect(() => {
    api.get('/api/bookings')
      .then(response => setBookings(response.data))
      .catch(() => {});
  }, []);

  const createBooking = async (event) => {
    event.preventDefault();
    try {
      const payload = {
        soNumber: `SO-${Math.floor(Math.random() * 100000)}`,
        sapAccountId: 'SAP-123',
        companyName,
        address,
        postalCode,
        bookingType: 'PULL_OUT',
        slotType: 'ROUTINE',
        testingType: 'NON_OT',
        slotDateTimeStart: slotDateTimeStart ? `${slotDateTimeStart}:00` : null,
        slotDateTimeEnd: slotDateTimeStart ? `${slotDateTimeStart}:00` : null,
        anchorDetail: {
          anchorCategory: 'Chemical',
          anchorType: 'HILTI',
          anchorSize: '10mm',
          anchorQuantity: 4,
          proofLoadValue: '10kN'
        },
        onsiteContacts: [
          {
            name: 'Site Contact',
            contactNumber: '+65 9123 4567',
            email: 'contact@example.com'
          }
        ]
      };
      await api.post('/api/bookings', payload);
      setMessage('Booking created successfully. Refresh the page to view it.');
    } catch (error) {
      setMessage('Unable to create booking.');
    }
  };

  return (
    <div className="container">
      <div className="navbar">
        <h1>Customer Dashboard</h1>
        <button className="button" onClick={logout}>Logout</button>
      </div>

      <div className="card" style={{ marginBottom: 24 }}>
        <h2>Create New Booking</h2>
        {message && <div style={{ marginBottom: 12 }}>{message}</div>}
        <form onSubmit={createBooking}>
          <input
            className="input-field"
            value={companyName}
            onChange={(e) => setCompanyName(e.target.value)}
            placeholder="Company name"
            required
          />
          <input
            className="input-field"
            value={address}
            onChange={(e) => setAddress(e.target.value)}
            placeholder="Address"
            required
          />
          <input
            className="input-field"
            value={postalCode}
            onChange={(e) => setPostalCode(e.target.value)}
            placeholder="Postal code"
            required
          />
          <input
            className="input-field"
            type="datetime-local"
            value={slotDateTimeStart}
            onChange={(e) => setSlotDateTimeStart(e.target.value)}
            required
          />
          <button className="button" type="submit">Create Booking</button>
        </form>
      </div>

      <div className="card">
        <p>Welcome {user?.email}, use this dashboard to view your bookings.</p>
        <table style={{ width: '100%', borderCollapse: 'collapse', marginTop: 16 }}>
          <thead>
            <tr>
              <th style={{ textAlign: 'left', padding: 8 }}>Booking #</th>
              <th style={{ textAlign: 'left', padding: 8 }}>Company</th>
              <th style={{ textAlign: 'left', padding: 8 }}>Status</th>
              <th style={{ textAlign: 'left', padding: 8 }}>Slot</th>
            </tr>
          </thead>
          <tbody>
            {bookings.map((booking) => (
              <tr key={booking.id}>
                <td style={{ padding: 8 }}>{booking.bookingNumber}</td>
                <td style={{ padding: 8 }}>{booking.companyName}</td>
                <td style={{ padding: 8 }}>{booking.status}</td>
                <td style={{ padding: 8 }}>{booking.slotDateTimeStart ? new Date(booking.slotDateTimeStart).toLocaleString() : '-'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
