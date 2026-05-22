import { useEffect, useState } from 'react';
import api from '../../services/api';
import { useAuth } from '../../context/AuthContext';

export default function AdminDashboard() {
  const { logout, user } = useAuth();
  const [bookings, setBookings] = useState([]);

  useEffect(() => {
    api.get('/api/bookings')
      .then(response => setBookings(response.data))
      .catch(() => {});
  }, []);

  return (
    <div className="container">
      <div className="navbar">
        <h1>Admin Dashboard</h1>
        <button className="button" onClick={logout}>Logout</button>
      </div>
      <div className="card">
        <p>Welcome {user?.email}. This view shows all booking records.</p>
        <table style={{ width: '100%', borderCollapse: 'collapse', marginTop: 16 }}>
          <thead>
            <tr>
              <th style={{ textAlign: 'left', padding: 8 }}>Booking #</th>
              <th style={{ textAlign: 'left', padding: 8 }}>Customer</th>
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
