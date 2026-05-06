import React, { useState } from 'react';
import { login } from '../services/api';

function LoginPage({ onLogin }) {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');

    const handleLogin = async () => {
        try {
            const response = await login(username, password);
            const jwt = response.data.jwt;
            localStorage.setItem('jwt', jwt);
            localStorage.setItem('user', JSON.stringify(response.data));
            onLogin(response.data);
        } catch (err) {
            setError('Login failed! Check your username and password.');
        }
    };

    return (
        <div style={styles.container}>
            <div style={styles.box}>
                <h2 style={styles.title}>🗞️ Kun.uz</h2>
                <h3 style={styles.subtitle}>Login</h3>

                {error && <p style={styles.error}>{error}</p>}

                <input
                    style={styles.input}
                    type="email"
                    placeholder="Email"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                />
                <input
                    style={styles.input}
                    type="password"
                    placeholder="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />
                <button style={styles.button} onClick={handleLogin}>
                    Login
                </button>
            </div>
        </div>
    );
}

const styles = {
    container: {
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        height: '100vh',
        backgroundColor: '#f0f2f5'
    },
    box: {
        backgroundColor: 'white',
        padding: '40px',
        borderRadius: '10px',
        boxShadow: '0 2px 10px rgba(0,0,0,0.1)',
        width: '350px',
        display: 'flex',
        flexDirection: 'column',
        gap: '15px'
    },
    title: {
        textAlign: 'center',
        color: '#e63946',
        margin: 0
    },
    subtitle: {
        textAlign: 'center',
        color: '#333',
        margin: 0
    },
    input: {
        padding: '12px',
        borderRadius: '6px',
        border: '1px solid #ddd',
        fontSize: '14px'
    },
    button: {
        padding: '12px',
        backgroundColor: '#e63946',
        color: 'white',
        border: 'none',
        borderRadius: '6px',
        fontSize: '16px',
        cursor: 'pointer'
    },
    error: {
        color: 'red',
        textAlign: 'center',
        margin: 0
    }
};

export default LoginPage;