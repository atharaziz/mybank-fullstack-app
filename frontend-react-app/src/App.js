import React from 'react';
import {BrowserRouter as Router, Route, Routes, Link} from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';
import Home from './Home';
import CreateCustomerForm from './CreateCustomerForm';
import EditCustomerForm from './EditCustomerForm';

const App = () => {
    return (
        <Router>
            <div className="container mt-5">
                <Routes>
                    <Route path="/" element={<Home/>}/>
                    <Route path="/create-customer" element={<CreateCustomerForm/>}/>
                    <Route path="/edit-customer/:id" element={<EditCustomerForm/>}/>
                </Routes>
            </div>
        </Router>
    );
};

export default App;
