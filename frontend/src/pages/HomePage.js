import React, { useState, useEffect } from 'react';
import { getSavedArticles, removeSavedArticle } from '../services/api';

function HomePage({ user, onLogout }) {
    const [savedArticles, setSavedArticles] = useState([]);
    const [message, setMessage] = useState('');

    useEffect(() => {
        loadSavedArticles();
    }, []);

    const loadSavedArticles = async () => {
        try {
            const response = await getSavedArticles();
            setSavedArticles(response.data);
        } catch (err) {
            console.error('Failed to load saved articles');
        }
    };

    const handleRemove = async (articleId) => {
        try {
            await removeSavedArticle(articleId);
            setMessage('Article removed!');
            loadSavedArticles();
        } catch (err) {
            setMessage('Failed to remove article!');
        }
    };

    return (
        <div style={styles.container}>
                    {/* CONTENT */}
            <div style={styles.content}>
                <h3>📌 Saved Articles</h3>
                {message && <p style={styles.message}>{message}</p>}

                {savedArticles.length === 0 ? (
                    <p style={styles.empty}>No saved articles yet!</p>
                ) : (
                    savedArticles.map((item) => (
                        <div key={item.id} style={styles.card}>
                            <p><strong>Article ID:</strong> {item.articleId}</p>
                            <p><strong>Saved:</strong> {new Date(item.createdDate).toLocaleDateString()}</p>
                            <button
                                style={styles.removeBtn}
                                onClick={() => handleRemove(item.articleId)}>
                                🗑️ Remove
                            </button>
                        </div>
                    ))
                )}
            </div>
        </div>
    );
}

const styles = {
    container: {
        minHeight: '100vh',
        backgroundColor: '#f0f2f5'
    },
    content: {
        maxWidth: '800px',
        margin: '30px auto',
        padding: '0 20px'
    },
    card: {
        backgroundColor: 'white',
        padding: '20px',
        borderRadius: '10px',
        boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
        marginBottom: '15px'
    },
    removeBtn: {
        padding: '8px 16px',
        backgroundColor: '#e63946',
        color: 'white',
        border: 'none',
        borderRadius: '6px',
        cursor: 'pointer'
    },
    message: {
        color: 'green',
        fontWeight: 'bold'
    },
    empty: {
        color: '#888',
        textAlign: 'center',
        marginTop: '50px',
        fontSize: '18px'
    }
};

export default HomePage;