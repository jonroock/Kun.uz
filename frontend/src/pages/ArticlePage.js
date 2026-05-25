import React, { useState, useEffect } from 'react';
import axios from 'axios';

const API_URL = 'http://localhost:8080/api/v1';

function ArticlesPage({ user }) {
    const [articles, setArticles] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [message, setMessage] = useState('');
    const [openComments, setOpenComments] = useState({});
    const [comments, setComments] = useState({});
    const [newComment, setNewComment] = useState({});

    useEffect(() => {
        loadArticles();
    }, []);

    const getHeaders = () => ({
        headers: { Authorization: `Bearer ${localStorage.getItem('jwt')}` }
    });

    const loadArticles = async () => {
        try {
            const response = await axios.get(`${API_URL}/article/by-region/1/10`);
            setArticles(response.data);
        } catch (err) {
            setError('Failed to load articles!');
        } finally {
            setLoading(false);
        }
    };

    const handleSave = async (articleId) => {
        try {
            await axios.post(`${API_URL}/saved-article`, { articleId }, getHeaders());
            showMessage('Article saved! ✅');
        } catch (err) {
            showMessage('Failed to save! ❌');
        }
    };

    const handleLike = async (articleId) => {
        try {
            await axios.post(`${API_URL}/article-like`, {
                articleId,
                emotion: 'LIKE'
            }, getHeaders());
            showMessage('Liked! ❤️');
            loadArticles();
        } catch (err) {
            showMessage('Failed to like! ❌');
        }
    };

    const handleDislike = async (articleId) => {
        try {
            await axios.post(`${API_URL}/article-like`, {
                articleId,
                emotion: 'DISLIKE'
            }, getHeaders());
            showMessage('Disliked! 👎');
            loadArticles();
        } catch (err) {
            showMessage('Failed! ❌');
        }
    };

    const toggleComments = async (articleId) => {
        const isOpen = openComments[articleId];
        setOpenComments(prev => ({ ...prev, [articleId]: !isOpen }));
        if (!isOpen && !comments[articleId]) {
            await loadComments(articleId);
        }
    };

    const loadComments = async (articleId) => {
        try {
            const response = await axios.get(`${API_URL}/comment/article/${articleId}`);
            setComments(prev => ({ ...prev, [articleId]: response.data.content || [] }));
        } catch (err) {
            console.error('Failed to load comments');
        }
    };

    const handleAddComment = async (articleId) => {
        const content = newComment[articleId];
        if (!content || !content.trim()) return;
        try {
            await axios.post(`${API_URL}/comment`, {
                content,
                articleId
            }, getHeaders());
            setNewComment(prev => ({ ...prev, [articleId]: '' }));
            await loadComments(articleId);
            showMessage('Comment added! 💬');
        } catch (err) {
            showMessage('Failed to add comment! ❌');
        }
    };

    const handleDeleteComment = async (commentId, articleId) => {
        try {
            await axios.delete(`${API_URL}/comment/${commentId}`, getHeaders());
            await loadComments(articleId);
            showMessage('Comment deleted!');
        } catch (err) {
            showMessage('Failed to delete! ❌');
        }
    };

    const showMessage = (msg) => {
        setMessage(msg);
        setTimeout(() => setMessage(''), 3000);
    };

    if (loading) return <div style={styles.center}>Loading articles...</div>;
    if (error) return <div style={{ ...styles.center, color: 'red' }}>{error}</div>;

    return (
        <div style={styles.container}>
            <h3 style={styles.pageTitle}>📰 Latest Articles</h3>
            {message && <p style={styles.message}>{message}</p>}

            {articles.length === 0 ? (
                <p style={styles.empty}>No published articles yet!</p>
            ) : (
                articles.map((article) => (
                    <div key={article.id} style={styles.card}>
                        {/* Article Header */}
                        <h4 style={styles.articleTitle}>{article.title}</h4>
                        <p style={styles.description}>{article.description}</p>

                        {/* Stats */}
                        <div style={styles.stats}>
                            <span>⏱️ {article.readTime} min</span>
                            <span>👁️ {article.viewCount || 0} views</span>
                            <span>❤️ {article.likeCount || 0} likes</span>
                        </div>

                        {/* Action Buttons */}
                        <div style={styles.actions}>
                            <button style={styles.likeBtn} onClick={() => handleLike(article.id)}>
                                ❤️ Like
                            </button>
                            <button style={styles.dislikeBtn} onClick={() => handleDislike(article.id)}>
                                👎 Dislike
                            </button>
                            <button style={styles.commentBtn} onClick={() => toggleComments(article.id)}>
                                💬 Comments {openComments[article.id] ? '▲' : '▼'}
                            </button>
                            <button style={styles.saveBtn} onClick={() => handleSave(article.id)}>
                                🔖 Save
                            </button>
                        </div>

                        {/* Comments Section */}
                        {openComments[article.id] && (
                            <div style={styles.commentsSection}>
                                <h5 style={styles.commentsTitle}>Comments</h5>

                                {/* Add Comment */}
                                <div style={styles.addComment}>
                                    <input
                                        style={styles.commentInput}
                                        type="text"
                                        placeholder="Write a comment..."
                                        value={newComment[article.id] || ''}
                                        onChange={(e) => setNewComment(prev => ({
                                            ...prev,
                                            [article.id]: e.target.value
                                        }))}
                                    />
                                    <button
                                        style={styles.postBtn}
                                        onClick={() => handleAddComment(article.id)}>
                                        Post
                                    </button>
                                </div>

                                {/* Comment List */}
                                {(comments[article.id] || []).length === 0 ? (
                                    <p style={styles.noComments}>No comments yet. Be the first!</p>
                                ) : (
                                    (comments[article.id] || []).map((comment) => (
                                        <div key={comment.id} style={styles.commentCard}>
                                            <p style={styles.commentContent}>{comment.content}</p>
                                            <div style={styles.commentFooter}>
                                                <span style={styles.commentMeta}>
                                                    {comment.profileName} · {new Date(comment.createdDate).toLocaleDateString()}
                                                </span>
                                                {comment.profileId === user.id && (
                                                    <button
                                                        style={styles.deleteBtn}
                                                        onClick={() => handleDeleteComment(comment.id, article.id)}>
                                                        🗑️
                                                    </button>
                                                )}
                                            </div>
                                        </div>
                                    ))
                                )}
                            </div>
                        )}
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
        marginBottom: '20px',
        fontSize: '22px'
    },
    card: {
        backgroundColor: 'white',
        padding: '24px',
        borderRadius: '10px',
        boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
        marginBottom: '20px'
    },
    articleTitle: {
        color: '#333',
        margin: '0 0 8px',
        fontSize: '18px'
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
        marginBottom: '12px'
    },
    actions: {
        display: 'flex',
        gap: '10px',
        flexWrap: 'wrap'
    },
    likeBtn: {
        padding: '8px 14px',
        backgroundColor: '#e63946',
        color: 'white',
        border: 'none',
        borderRadius: '6px',
        cursor: 'pointer'
    },
    dislikeBtn: {
        padding: '8px 14px',
        backgroundColor: '#555',
        color: 'white',
        border: 'none',
        borderRadius: '6px',
        cursor: 'pointer'
    },
    commentBtn: {
        padding: '8px 14px',
        backgroundColor: '#457b9d',
        color: 'white',
        border: 'none',
        borderRadius: '6px',
        cursor: 'pointer'
    },
    saveBtn: {
        padding: '8px 14px',
        backgroundColor: '#2a9d8f',
        color: 'white',
        border: 'none',
        borderRadius: '6px',
        cursor: 'pointer'
    },
    commentsSection: {
        marginTop: '16px',
        borderTop: '1px solid #eee',
        paddingTop: '16px'
    },
    commentsTitle: {
        margin: '0 0 12px',
        color: '#333'
    },
    addComment: {
        display: 'flex',
        gap: '8px',
        marginBottom: '16px'
    },
    commentInput: {
        flex: 1,
        padding: '10px',
        borderRadius: '6px',
        border: '1px solid #ddd',
        fontSize: '14px'
    },
    postBtn: {
        padding: '10px 16px',
        backgroundColor: '#e63946',
        color: 'white',
        border: 'none',
        borderRadius: '6px',
        cursor: 'pointer'
    },
    commentCard: {
        backgroundColor: '#f8f9fa',
        padding: '12px',
        borderRadius: '6px',
        marginBottom: '8px'
    },
    commentContent: {
        margin: '0 0 6px',
        color: '#333',
        fontSize: '14px'
    },
    commentFooter: {
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center'
    },
    commentMeta: {
        color: '#888',
        fontSize: '12px'
    },
    deleteBtn: {
        background: 'none',
        border: 'none',
        cursor: 'pointer',
        fontSize: '14px'
    },
    message: {
        textAlign: 'center',
        fontWeight: 'bold',
        color: 'green'
    },
    noComments: {
        color: '#888',
        textAlign: 'center',
        fontSize: '14px'
    },
    center: {
        textAlign: 'center',
        marginTop: '100px',
        fontSize: '18px'
    },
    empty: {
        color: '#888',
        textAlign: 'center',
        marginTop: '50px',
        fontSize: '18px'
    }
};

export default ArticlesPage;