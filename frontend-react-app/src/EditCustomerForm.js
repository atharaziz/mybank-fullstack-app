import React, {useState, useEffect} from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import {useParams, useNavigate} from 'react-router-dom';

const EditCustomerForm = () => {
    const {id} = useParams();
    const navigate = useNavigate();
    const [customer, setCustomer] = useState({name: '', email: '', phone: '', dateOfBirth: ''});
    const [alertMessage, setAlertMessage] = useState('');  // Merged error and formError into one
    const [successMessage, setSuccessMessage] = useState('');
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        const fetchCustomer = async () => {
            try {
                const response = await fetch(`http://localhost:8089/api/bank/getCustomer/${id}`);
                if (!response.ok) throw new Error('Failed to fetch customer');
                const data = await response.json();
                setCustomer(data);
            } catch (err) {
                setAlertMessage('Customer not found');
            }
        };
        fetchCustomer();
    }, [id]);

    const handleChange = (e) => {
        const {name, value} = e.target;
        setCustomer((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const validateForm = () => {
        if (!customer.name || !customer.email || !customer.phone || !customer.dateOfBirth) {
            return 'All fields are required!';
        }

        const nameRegex = /^[A-Za-z\s]+$/;
        if (!nameRegex.test(customer.name)) {
            return 'Name must contain only alphabets.';
        }

        const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
        if (!emailRegex.test(customer.email)) {
            return 'Please enter a valid email address.';
        }

        const phoneRegex = /^[0-9]{9,15}$/;
        if (!phoneRegex.test(customer.phone)) {
            return 'Phone number must be between 9 and 15 digits.';
        }
        return '';
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setAlertMessage('');  // Clear previous error messages

        const validationError = validateForm();
        if (validationError) {
            setAlertMessage(validationError);
            setLoading(false);
            return;
        }

        try {
            const response = await fetch(`http://localhost:8089/api/bank/updateCustomer/${id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(customer),
            });

            if (!response.ok) {
                throw new Error('Failed to update customer');
            }

            const data = await response.json();
            console.log('Customer updated:', data);
            setLoading(false);
            localStorage.setItem('customerSuccessMessage', 'Customer updated successfully!');
            navigate('/');


        } catch (err) {
            setLoading(false);
            setAlertMessage('An error occurred while updating the customer');
        }
    };

    const closeAlert = () => {
        setAlertMessage('');  // Clear the alert message when closed
    };

    return (
        <div className="container mt-5">
            <h2>Edit Customer</h2>

            {/* Success Alert */}
            {successMessage && (
                <div className="alert alert-success alert-dismissible fade show" role="alert">
                    {successMessage}
                    <button
                        type="button"
                        className="btn-close"
                        aria-label="Close"
                        onClick={() => closeAlert()}
                    />
                </div>
            )}

            {/* Error Alerts */}
            {alertMessage && (
                <div className="alert alert-danger alert-dismissible fade show" role="alert">
                    {alertMessage}
                    <button
                        type="button"
                        className="btn-close"
                        aria-label="Close"
                        onClick={() => closeAlert()}
                    />
                </div>
            )}

            <div className="mt-4">
                <button className="btn btn-secondary" onClick={() => navigate('/')}>
                    <i className="bi bi-arrow-left mr-2"/> Back to Home
                </button>
            </div>

            <form onSubmit={handleSubmit} className="needs-validation" noValidate>
                <div className="mb-3">
                    <label htmlFor="name" className="form-label">
                        Name:
                    </label>
                    <input
                        type="text"
                        id="name"
                        name="name"
                        value={customer.name}
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
                        value={customer.email}
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
                        value={customer.phone}
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
                        value={customer.dateOfBirth}
                        onChange={handleChange}
                        className="form-control"
                        required
                    />
                </div>

                <button type="submit" className="btn btn-primary" disabled={loading}>
                    {loading ? (
                        <span className="spinner-border spinner-border-sm" role="status" aria-hidden="true"/>
                    ) : (
                        'Update Customer'
                    )}
                </button>
            </form>
        </div>
    );
};

export default EditCustomerForm;
