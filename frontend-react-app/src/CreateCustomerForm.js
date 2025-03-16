import React, { useState } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import { useNavigate } from 'react-router-dom';

const CreateCustomerForm = () => {
    const navigate = useNavigate();

    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [phone, setPhone] = useState('');
    const [dateOfBirth, setDateOfBirth] = useState('');
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [loading, setLoading] = useState(false);

    // Handle input changes
    const handleChange = (e) => {
        const { name, value } = e.target;
        if (name === 'name') setName(value);
        if (name === 'email') setEmail(value);
        if (name === 'phone') setPhone(value);
        if (name === 'dateOfBirth') setDateOfBirth(value);
    };

    // Validate form inputs
    const validateForm = () => {
        if (!name || !email || !phone || !dateOfBirth) {
            return 'All fields are required!';
        }

        // Name should only contain alphabets and spaces
        const nameRegex = /^[A-Za-z\s]+$/;
        if (!nameRegex.test(name)) {
            return 'Name must contain only alphabets and spaces.';
        }

        // Email validation (simple pattern)
        const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
        if (!emailRegex.test(email)) {
            return 'Please enter a valid email address.';
        }

        // Phone number should be numeric and between 9 to 15 digits
        const phoneRegex = /^[0-9]{9,15}$/;
        if (!phoneRegex.test(phone)) {
            return 'Phone number must be between 9 and 15 digits.';
        }

        return ''; // Return an empty string if everything is valid
    };

    // Handle form submission
    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');
        setLoading(true);

        const formError = validateForm(); // Run validation before submitting
        if (formError) {
            setError(formError);
            setLoading(false);
            return; // Stop the submission if validation fails
        }

        const apiUrl = 'http://localhost:8089/api/bank/createCustomer';

        try {
            const response = await fetch(apiUrl, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    name,
                    email,
                    phone,
                    dateOfBirth,
                }),
            });

            if (!response.ok) {
                throw new Error('Failed to create customer');
            }

            const data = await response.json();
            console.log('Customer created:', data);
            setLoading(false);
            setSuccess('Customer created successfully!');

            // Save the success message to localStorage
            localStorage.setItem('customerSuccessMessage', 'Customer created successfully!');

            setName('');
            setEmail('');
            setPhone('');
            setDateOfBirth('');

            // Redirect to home page after creation
            navigate('/');
        } catch (err) {
            console.error('Error creating customer:', err);
            setLoading(false);
            setError('An error occurred. Please try again.');
            setTimeout(() => {
                setError('');
            }, 5000);
        }
    };

    // Function to close alert
    const closeAlert = (type) => {
        if (type === 'error') {
            setError('');
        } else if (type === 'success') {
            setSuccess('');
        }
    };

    return (
        <div className="container mt-5">
            <h2>Create Customer</h2>

            {/* Success Message with close button */}
            {success && (
                <div className="alert alert-success alert-dismissible fade show" role="alert">
                    {success}
                    <button type="button" className="btn-close" aria-label="Close" onClick={() => closeAlert('success')}></button>
                </div>
            )}

            {/* Error Message with close button */}
            {error && (
                <div className="alert alert-danger alert-dismissible fade show" role="alert">
                    {error}
                    <button type="button" className="btn-close" aria-label="Close" onClick={() => closeAlert('error')}></button>
                </div>
            )}

            <button
                className="btn btn-secondary mb-4"
                onClick={() => navigate('/')} // Navigate back to Home
            >
                Back to Home
            </button>

            <form onSubmit={handleSubmit} className="needs-validation" noValidate>
                <div className="mb-3">
                    <label htmlFor="name" className="form-label">
                        Name:
                    </label>
                    <input
                        type="text"
                        id="name"
                        name="name"
                        value={name}
                        onChange={handleChange}
                        className="form-control"
                        required
                    />
                </div>

                <div className="mb-3">
                    <label htmlFor="email" className="form-label">
                        Email:
                    </label>
                    <input
                        type="email"
                        id="email"
                        name="email"
                        value={email}
                        onChange={handleChange}
                        className="form-control"
                        required
                    />
                </div>

                <div className="mb-3">
                    <label htmlFor="phone" className="form-label">
                        Phone:
                    </label>
                    <input
                        type="text"
                        id="phone"
                        name="phone"
                        value={phone}
                        onChange={handleChange}
                        className="form-control"
                        required
                    />
                </div>

                <div className="mb-3">
                    <label htmlFor="dateOfBirth" className="form-label">
                        Date of Birth:
                    </label>
                    <input
                        type="date"
                        id="dateOfBirth"
                        name="dateOfBirth"
                        value={dateOfBirth}
                        onChange={handleChange}
                        className="form-control"
                        required
                    />
                </div>

                <button type="submit" className="btn btn-primary" disabled={loading}>
                    {loading ? (
                        <span className="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
                    ) : (
                        'Create Customer'
                    )}
                </button>
            </form>
        </div>
    );
};

export default CreateCustomerForm;
