import React, {useState, useEffect} from 'react';
import {useNavigate} from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';
import './App.css';

const CustomerList = ({customers, onEdit, onDelete}) => {
    if (customers.length === 0) {
        return (
            <div>
                <h4>No customers found.</h4>
                <h5>Click on Create Customer button to add new customer.</h5>
            </div>
        );
    }

    return (
        <div>
            <h1 className="mb-4">Customer List</h1>
            <table className="table table-striped">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Phone</th>
                    <th>Date of Birth</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {customers.map((customer) => (
                    <tr key={customer.id}>
                        <td>{customer.id}</td>
                        <td>{customer.name}</td>
                        <td>{customer.email}</td>
                        <td>{customer.phone}</td>
                        <td>{new Date(customer.dateOfBirth).toLocaleDateString()}</td>
                        <td>
                            <button
                                className="btn btn-primary btn-sm mr-2"
                                onClick={() => onEdit(customer.id)}
                            >
                                Edit
                            </button>
                            <button
                                className="btn btn-danger btn-sm"
                                onClick={() => onDelete(customer.id)}
                            >
                                Delete
                            </button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};

const Home = () => {
    const navigate = useNavigate();
    const [customers, setCustomers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [successMessage, setSuccessMessage] = useState('');

    useEffect(() => {
        // Fetch customers data from API
        const fetchCustomers = async () => {
            try {
                const response = await fetch('http://localhost:8089/api/bank/getCustomers');
                if (response.status === 404) {
                    setCustomers([]);
                } else if (!response.ok) {
                    throw new Error('Data fetch failed. Please check if the APIs are accessible and ensure there are no network issues or server outages.');
                } else {
                    const data = await response.json();
                    setCustomers(data);
                }
            } catch (error) {
                setError(error.message);
            } finally {
                setLoading(false);
            }
        };

        fetchCustomers();

        // Check if there is a success message in localStorage
        const message = localStorage.getItem('customerSuccessMessage');
        if (message) {
            setSuccessMessage(message);
            localStorage.removeItem('customerSuccessMessage'); // Clear the success message
        }
    }, []);

    const handleEdit = (customerId) => {
        navigate(`/edit-customer/${customerId}`);
    };

    const handleDelete = async (customerId) => {
        // Show confirmation dialog before proceeding
        const isConfirmed = window.confirm('Are you sure you want to delete this customer?');

        if (isConfirmed) {
            try {
                const response = await fetch(`http://localhost:8089/api/bank/deleteCustomer/${customerId}`, {
                    method: 'DELETE',
                });

                if (!response.ok) {
                    throw new Error('Failed to delete customer');
                }

                // Remove the customer from the state after successful deletion
                setCustomers(customers.filter((customer) => customer.id !== customerId));

                // Set success message
                setSuccessMessage(`Successfully deleted customer with ID: ${customerId}`);
            } catch (error) {
                alert(`Error: ${error.message}`);
            }
        } else {
            // If the user cancels the delete action
            console.log('Customer deletion canceled');
        }
    };

    const handleCreateCustomer = () => {
        navigate('/create-customer');
    };

    const closeSuccessAlert = () => {
        setSuccessMessage('');
    };

    if (loading) return <p>Loading...</p>;
    if (error) return <p>Error: {error}</p>;

    return (
        <div className="container">
            <h1 className="text-center my-4">Welcome to the Customer Management</h1>

            {/* Display Success Message with Close Button */}
            {successMessage && (
                <div className="alert alert-success alert-dismissible fade show" role="alert">
                    {successMessage}
                    <button
                        type="button"
                        className="btn-close"
                        aria-label="Close"
                        onClick={closeSuccessAlert}
                    />
                </div>
            )}

            <button
                className="btn btn-success mb-4"
                onClick={handleCreateCustomer}
            >
                Create Customer
            </button>

            <CustomerList
                customers={customers}
                onEdit={handleEdit}
                onDelete={handleDelete}
            />

            {/* Footer */}
            <footer className="mt-4 text-center text-muted">
                <hr/>
                <p>&copy; 2025 Customer Management. All rights reserved.</p>
            </footer>
        </div>
    );
};

export default Home;
