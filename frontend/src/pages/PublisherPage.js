import React, { useState, useEffect } from 'react';
import axios from 'axios';

const API_URL = 'http://localhost:8080/api/v1';

function PublisherPage() {
    const [articles, setArticles] = useState([]);
    const [loading, setLoading] = useState(true);
    const [message, setMessage] = useState('');

    const getHeaders = () => ({
        headers: { Authorization: `Bearer ${localStorage.getItem('jwt')}` }
    });

    useEffect(() => {
        loadUnpublished();
    }, []);

    const loadUnpublished = async () => {
        setLoading(true);
        try {
            const response = await axios.post(
                `${API_URL}/article/moderator/filter?page=1&size=20`,
                {},
                getHeaders()
            );
            setArticles(response.data.content || []);
        } catch (err) {
            showMessage('Failed to load articles! ❌');
        } finally {
            setLoading(false);
        }
    };

    const handlePublish = async (articleId) => {
        try {
            await axios.put(
                `${API_URL}/article/moderator/${articleId}/status`,
                { status: 'PUBLISHED' },
                getHeaders()
            );
            showMessage('Article published! ✅');
            loadUnpublished();
        } catch (err) {
            showMessage('Failed to publish! ❌');
        }
    };

    const handleReject = async (articleId) => {
        try {
            await axios.delete(
                `${API_URL}/article/moderator/${articleId}`,
                getHeaders()
            );
            showMessage('Article rejected and deleted!');
            loadUnpublished();
        } catch (err) {
            showMessage('Failed to reject! ❌');
        }
    };

    const showMessage = (msg) => {
        setMessage(msg);
        setTimeout(() => setMessage(''), 3000);
    };

    return (
        <div style={styles.container}>
            <h3 style={styles.pageTitle}>📋 Publisher Dashboard</h3>
            <p style={styles.subtitle}>Review and publish articles submitted by moderators</p>

            {message && <p style={styles.message}>{message}</p>}

            {loading ? (
                <p style={styles.empty}>Loading...</p>
            ) : articles.length === 0 ? (
                <div style={styles.emptyBox}>
                    <p style={styles.emptyIcon}>✅</p>
                    <p style={styles.empty}>No articles waiting for review!</p>
                </div>
            ) : (
                articles.map((article) => (
                    <div key={article.id} style={styles.card}>
                        <div style={styles.cardHeader}>
                            <h4 style={styles.articleTitle}>{article.title}</h4>
                            <span style={styles.badge}>⏳ Pending</span>
                        </div>
                        <p style={styles.description}>{article.description}</p>
                        <div style={styles.stats}>
                            <span>⏱️ {article.readTime || 0} min read</span>
                            <span>📅 {article.createdDate ? new Date(article.createdDate).toLocaleDateString() : ''}</span>
                        </div>
                        <div style={styles.actions}>
                            <button
                                style={styles.publishBtn}
                                onClick={() => handlePublish(article.id)}>
                                ✅ Publish
                            </button>
                            <button
                                style={styles.rejectBtn}
                                onClick={() => handleReject(article.id)}>
                                ❌ Reject
                            </button>
                        </div>
                    </div>
                ))
            )}
        </div>
    );
}

const styles = {
    container: {
        maxWidth: '800px',
        margin: '30px auto',
        padding: '0 20px'
    },
    pageTitle: {
        color: '#333',
        fontSize: '22px',
        margin: '0 0 6px'
    },
    subtitle: {
        color: '#888',
        fontSize: '14px',
        margin: '0 0 24px'
    },
    card: {
        backgroundColor: 'white',
        padding: '24px',
        borderRadius: '10px',
        boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
        marginBottom: '16px'
    },
    cardHeader: {
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        marginBottom: '10px'
    },
    articleTitle: {
        color: '#333',
        margin: 0,
        fontSize: '18px'
    },
    badge: {
        backgroundColor: '#fff3cd',
        color: '#856404',
        padding: '4px 10px',
        borderRadius: '20px',
        fontSize: '12px',
        fontWeight: 'bold'
    },
    description: {
        color: '#666',
        fontSize: '14px',
        margin: '0 0 12px'
    },
    stats: {
        display: 'flex',
        gap: '16px',
        color: '#888',
        fontSize: '13px',
        marginBottom: '16px'
    },
    actions: {
        display: 'flex',
        gap: '10px'
    },
    publishBtn: {
        padding: '10px 20px',
        backgroundColor: '#2a9d8f',
        color: 'white',
        border: 'none',
        borderRadius: '6px',
        cursor: 'pointer',
        fontWeight: 'bold',
        fontSize: '14px'
    },
    rejectBtn: {
        padding: '10px 20px',
        backgroundColor: '#e63946',
        color: 'white',
        border: 'none',
        borderRadius: '6px',
        cursor: 'pointer',
        fontWeight: 'bold',
        fontSize: '14px'
    },
    message: {
        textAlign: 'center',
        fontWeight: 'bold',
        color: 'green',
        marginBottom: '16px'
    },
    emptyBox: {
        textAlign: 'center',
        marginTop: '60px'
    },
    emptyIcon: {
        fontSize: '48px',
        margin: '0 0 10px'
    },
    empty: {
        color: '#888',
        textAlign: 'center',
        fontSize: '18px'
    }
};

export default PublisherPage;