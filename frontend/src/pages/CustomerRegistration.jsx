import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../services/api';
import './CustomerRegistration.css';

export default function CustomerRegistration() {
  const [formData, setFormData] = useState({
    email: '',
    firstName: '',
    lastName: '',
    companyName: '',
    accountNumber: '',
    contactNumber: '',
  });
  const [errors, setErrors] = useState({});
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const validateForm = () => {
    const newErrors = {};

    // Email validation
    if (!formData.email) {
      newErrors.email = 'Email is required';
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      newErrors.email = 'Please enter a valid email';
    }

    // First Name validation
    if (!formData.firstName) {
      newErrors.firstName = 'First Name is required';
    } else if (formData.firstName.length < 2) {
      newErrors.firstName = 'First Name must be at least 2 characters';
    } else if (formData.firstName.length > 50) {
      newErrors.firstName = 'First Name cannot exceed 50 characters';
    }

    // Last Name validation
    if (!formData.lastName) {
      newErrors.lastName = 'Last Name is required';
    } else if (formData.lastName.length < 2) {
      newErrors.lastName = 'Last Name must be at least 2 characters';
    } else if (formData.lastName.length > 50) {
      newErrors.lastName = 'Last Name cannot exceed 50 characters';
    }

    // Company Name validation
    if (!formData.companyName) {
      newErrors.companyName = 'Company Name is required';
    } else if (formData.companyName.length < 2) {
      newErrors.companyName = 'Company Name must be at least 2 characters';
    } else if (formData.companyName.length > 100) {
      newErrors.companyName = 'Company Name cannot exceed 100 characters';
    }

    // Account Number validation
    if (!formData.accountNumber) {
      newErrors.accountNumber = 'Customer Account Number is required';
    } else if (formData.accountNumber.length < 3) {
      newErrors.accountNumber = 'Account Number must be at least 3 characters';
    } else if (formData.accountNumber.length > 50) {
      newErrors.accountNumber = 'Account Number cannot exceed 50 characters';
    }

    // Contact Number validation
    if (!formData.contactNumber) {
      newErrors.contactNumber = 'Contact Number is required';
    } else if (!/^\+65\d{8}$/.test(formData.contactNumber)) {
      newErrors.contactNumber = 'Contact number must be in format +65XXXXXXXX (e.g., +6587654321)';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    // Clear error for this field when user starts typing
    if (errors[name]) {
      setErrors(prev => ({
        ...prev,
        [name]: ''
      }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage('');

    if (!validateForm()) {
      return;
    }

    setLoading(true);
    try {
      const response = await api.post('/api/auth/register-customer', formData);
      setMessage(response.data.message || 'Registration submitted successfully!');
      setTimeout(() => {
        navigate('/login');
      }, 2000);
    } catch (error) {
      const errorMsg = error.response?.data?.message || 'Registration failed. Please try again.';
      setMessage(errorMsg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="registration-container">
      <div className="registration-card">
        <h1 className="registration-title">Customer Registration</h1>
        <p className="registration-subtitle">Please enter your information below. Your registration will require admin approval.</p>

        {message && (
          <div className={`message ${message.includes('success') || message.includes('submitted') ? 'message-success' : 'message-error'}`}>
            {message}
          </div>
        )}

        <form onSubmit={handleSubmit} className="registration-form">
          {/* Email */}
          <div className="form-group">
            <label htmlFor="email">Email Address *</label>
            <input
              id="email"
              className={`input-field ${errors.email ? 'input-error' : ''}`}
              type="email"
              name="email"
              value={formData.email}
              onChange={handleInputChange}
              placeholder="Enter your email address"
              required
            />
            {errors.email && <span className="error-text">{errors.email}</span>}
          </div>

          {/* First Name */}
          <div className="form-group">
            <label htmlFor="firstName">First Name *</label>
            <input
              id="firstName"
              className={`input-field ${errors.firstName ? 'input-error' : ''}`}
              type="text"
              name="firstName"
              value={formData.firstName}
              onChange={handleInputChange}
              placeholder="Enter your first name"
              required
            />
            {errors.firstName && <span className="error-text">{errors.firstName}</span>}
          </div>

          {/* Last Name */}
          <div className="form-group">
            <label htmlFor="lastName">Last Name *</label>
            <input
              id="lastName"
              className={`input-field ${errors.lastName ? 'input-error' : ''}`}
              type="text"
              name="lastName"
              value={formData.lastName}
              onChange={handleInputChange}
              placeholder="Enter your last name"
              required
            />
            {errors.lastName && <span className="error-text">{errors.lastName}</span>}
          </div>

          {/* Company Name */}
          <div className="form-group">
            <label htmlFor="companyName">Company Name *</label>
            <input
              id="companyName"
              className={`input-field ${errors.companyName ? 'input-error' : ''}`}
              type="text"
              name="companyName"
              value={formData.companyName}
              onChange={handleInputChange}
              placeholder="Enter your company name"
              required
            />
            {errors.companyName && <span className="error-text">{errors.companyName}</span>}
          </div>

          {/* Account Number */}
          <div className="form-group">
            <label htmlFor="accountNumber">Customer Account Number *</label>
            <input
              id="accountNumber"
              className={`input-field ${errors.accountNumber ? 'input-error' : ''}`}
              type="text"
              name="accountNumber"
              value={formData.accountNumber}
              onChange={handleInputChange}
              placeholder="Enter your account number"
              required
            />
            {errors.accountNumber && <span className="error-text">{errors.accountNumber}</span>}
          </div>

          {/* Contact Number */}
          <div className="form-group">
            <label htmlFor="contactNumber">Contact Number (+65XXXXXXXX) *</label>
            <input
              id="contactNumber"
              className={`input-field ${errors.contactNumber ? 'input-error' : ''}`}
              type="tel"
              name="contactNumber"
              value={formData.contactNumber}
              onChange={handleInputChange}
              placeholder="+6587654321"
              required
            />
            {errors.contactNumber && <span className="error-text">{errors.contactNumber}</span>}
            <small className="help-text">Format: +65 followed by 8 digits (e.g., +6587654321)</small>
          </div>

          {/* Submit Button */}
          <button
            className="button button-primary"
            type="submit"
            disabled={loading}
          >
            {loading ? 'Submitting...' : 'Submit Registration'}
          </button>
        </form>

        {/* Login Link */}
        <div className="login-link">
          <p>Already have an account? <Link to="/login">Login here</Link></p>
        </div>
      </div>
    </div>
  );
}
