import React, { useState } from 'react';
import { register } from '../services/api';
import axios from 'axios';

const API_URL = 'http://localhost:8080/api/v1';

function RegisterPage({ onSwitch }) {
    const [name, setName] = useState('');
    const [surname, setSurname] = useState('');
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [step, setStep] = useState('register'); // 'register' or 'verify'
    const [code, setCode] = useState('');
    const [message, setMessage] = useState('');

    const handleRegister = async () => {
        setError('');
        if (!name || !surname || !username || !password) {
            setError('Please fill in all fields.');
            return;
        }
        try {
            await register(name, surname, username, password);
            setStep('verify');
        } catch (err) {
            setError('Registration failed! Username may already be taken.');
        }
    };

    const handleVerify = async () => {
        setError('');
        if (!code.trim()) {
            setError('Please enter the code.');
            return;
        }
        try {
            await axios.post(`${API_URL}/auth/registration/email/verification/code`, {
                phoneNumber: username,
                code: code
            });
            setMessage('Verified! You can now login. ✅');
            setTimeout(() => onSwitch(), 2000);
        } catch (err) {
            setError('Wrong or expired code. Try again.');
        }
    };

    return (
        <div style={styles.container}>
            <div style={styles.box}>
                <h2 style={styles.title}>🗞️ Kun.uz</h2>

                {step === 'register' ? (
                    <>
                        <h3 style={styles.subtitle}>Register</h3>
                        {error && <p style={styles.error}>{error}</p>}

                        <input style={styles.input} type="text" placeholder="Name"
                               value={name} onChange={(e) => setName(e.target.value)} />
                        <input style={styles.input} type="text" placeholder="Surname"
                               value={surname} onChange={(e) => setSurname(e.target.value)} />
                        <input style={styles.input} type="email" placeholder="Email"
                               value={username} onChange={(e) => setUsername(e.target.value)} />
                        <input style={styles.input} type="password" placeholder="Password"
                               value={password} onChange={(e) => setPassword(e.target.value)} />
                        <button style={styles.button} onClick={handleRegister}>Register</button>
                        <p style={styles.switchText}>
                            Already have an account?{' '}
                            <span style={styles.switchLink} onClick={onSwitch}>Login</span>
                        </p>
                    </>
                ) : (
                    <>
                        <h3 style={styles.subtitle}>Check your email 📧</h3>
                        <p style={styles.hint}>We sent a verification code to <b>{username}</b></p>
                        {error && <p style={styles.error}>{error}</p>}
                        {message && <p style={styles.success}>{message}</p>}

                        <input style={styles.input} type="text" placeholder="Enter 5-digit code"
                               value={code} onChange={(e) => setCode(e.target.value)} />
                        <button style={styles.button} onClick={handleVerify}>Verify</button>
                        <p style={styles.switchText}>
                            <span style={styles.switchLink} onClick={() => setStep('register')}>
                                ← Back to register
                            </span>
                        </p>
                    </>
                )}
            </div>
        </div>
    );
}

const styles = {
    container: {
        display: 'flex', justifyContent: 'center', alignItems: 'center',
        height: '100vh', backgroundColor: '#f0f2f5'
    },
    box: {
        backgroundColor: 'white', padding: '40px', borderRadius: '10px',
        boxShadow: '0 2px 10px rgba(0,0,0,0.1)', width: '350px',
        display: 'flex', flexDirection: 'column', gap: '15px'
    },
    title: { textAlign: 'center', color: '#e63946', margin: 0 },
    subtitle: { textAlign: 'center', color: '#333', margin: 0 },
    hint: { textAlign: 'center', color: '#666', fontSize: '14px', margin: 0 },
    input: { padding: '12px', borderRadius: '6px', border: '1px solid #ddd', fontSize: '14px' },
    button: {
        padding: '12px', backgroundColor: '#e63946', color: 'white',
        border: 'none', borderRadius: '6px', fontSize: '16px', cursor: 'pointer'
    },
    error: { color: 'red', textAlign: 'center', margin: 0 },
    success: { color: 'green', textAlign: 'center', margin: 0 },
    switchText: { textAlign: 'center', fontSize: '14px', margin: 0 },
    switchLink: { color: '#e63946', cursor: 'pointer', fontWeight: 'bold' }
};

export default RegisterPage;