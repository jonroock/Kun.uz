import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useLocation } from 'react-router-dom';

const API_URL = 'http://localhost:8080/api/v1';

function ArticlesPage({ user }) {
    const location = useLocation();
    const [articles, setArticles] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [message, setMessage] = useState('');
    const [likeCounts, setLikeCounts] = useState({});
    const [likedArticles, setLikedArticles] = useState({});
    const [openComments, setOpenComments] = useState({});
    const [comments, setComments] = useState({});
    const [newComment, setNewComment] = useState({});
    const [selectedCategory, setSelectedCategory] = useState(null);
    const [selectedSection, setSelectedSection] = useState(null);
    const [selectedRegion, setSelectedRegion] = useState(null);

    const getHeaders = () => ({
        headers: { Authorization: `Bearer ${localStorage.getItem('jwt')}` }
    });

    useEffect(() => {
        const state = location.state || {};
        setSelectedCategory(state.categoryId || null);
        setSelectedSection(state.sectionId || null);
        setSelectedRegion(state.regionId || null);
    }, [location.state]);

    useEffect(() => {
        loadArticles();
    }, [selectedCategory, selectedSection, selectedRegion]);

    const loadArticles = async () => {
        setLoading(true);
        try {
            const response = await axios.post(`${API_URL}/article/filter`, {
                categoryId: selectedCategory || null,
                regionId: selectedRegion || null,
                sectionId: selectedSection || null
            });
            const articleList = response.data.content || [];
            setArticles(articleList);
            const counts = {};
            const liked = {};
            await Promise.all(articleList.map(async (article) => {
                try {
                    const res = await axios.get(`${API_URL}/article-like/count/${article.id}`);
                    counts[article.id] = res.data;
                } catch { counts[article.id] = 0; }
                try {
                    const res = await axios.get(`${API_URL}/article-like/my/${article.id}`, getHeaders());
                    liked[article.id] = res.data;
                } catch { liked[article.id] = false; }
            }));
            setLikeCounts(counts);
            setLikedArticles(liked);
        } catch (err) {
            setError('Failed to load articles!');
        } finally {
            setLoading(false);
        }
    };

    const handleLike = async (articleId) => {
        try {
            if (likedArticles[articleId]) {
                await axios.put(`${API_URL}/article-like/remove/${articleId}`, {}, getHeaders());
                setLikeCounts(prev => ({ ...prev, [articleId]: Math.max((prev[articleId] || 1) - 1, 0) }));
                setLikedArticles(prev => ({ ...prev, [articleId]: false }));
                showMessage('Like removed! 🤍');
            } else {
                await axios.post(`${API_URL}/article-like`, { articleId, emotion: 'LIKE' }, getHeaders());
                setLikeCounts(prev => ({ ...prev, [articleId]: (prev[articleId] || 0) + 1 }));
                setLikedArticles(prev => ({ ...prev, [articleId]: true }));
                showMessage('Liked! ❤️');
            }
        } catch { showMessage('Failed! ❌'); }
    };

    const handleSave = async (articleId) => {
        try {
            await axios.post(`${API_URL}/saved-article`, { articleId }, getHeaders());
            showMessage('Article saved! ✅');
        } catch { showMessage('Failed to save! ❌'); }
    };

    const toggleComments = async (articleId) => {
        const isOpen = openComments[articleId];
        setOpenComments(prev => ({ ...prev, [articleId]: !isOpen }));
        if (!isOpen && !comments[articleId]) {
            try {
                const response = await axios.get(`${API_URL}/comment/article/${articleId}`);
                setComments(prev => ({ ...prev, [articleId]: response.data.content || [] }));
            } catch { }
        }
    };

    const handleAddComment = async (articleId) => {
        const content = newComment[articleId];
        if (!content || !content.trim()) return;
        try {
            await axios.post(`${API_URL}/comment`, { content, articleId }, getHeaders());
            setNewComment(prev => ({ ...prev, [articleId]: '' }));
            const res = await axios.get(`${API_URL}/comment/article/${articleId}`);
            setComments(prev => ({ ...prev, [articleId]: res.data.content || [] }));
            showMessage('Comment added! 💬');
        } catch { showMessage('Failed to comment! ❌'); }
    };

    const showMessage = (msg) => {
        setMessage(msg);
        setTimeout(() => setMessage(''), 3000);
    };

    if (error) return <div style={styles.center}>{error}</div>;

    return (
        <div style={styles.container}>
            {message && <p style={styles.message}>{message}</p>}

            {loading ? (
                <p style={styles.center}>Loading...</p>
            ) : articles.length === 0 ? (
                <p style={styles.center}>No articles found!</p>
            ) : (
                articles.map((article) => (
                    <div key={article.id} style={styles.card}>
                        <div style={styles.catPill}>{article.categoryName || 'General'}</div>
                        <h4 style={styles.articleTitle}>{article.title}</h4>
                        <p style={styles.description}>{article.description}</p>
                        <div style={styles.stats}>
                            <span>⏱️ {article.readTime || 0} min</span>
                            <span>👁️ {article.viewCount || 0}</span>
                            <span style={{ color: '#e63946' }}>❤️ {likeCounts[article.id] || 0}</span>
                        </div>
                        <div style={styles.actions}>
                            <button
                                style={likedArticles[article.id] ? styles.likedBtn : styles.likeBtn}
                                onClick={() => handleLike(article.id)}>
                                {likedArticles[article.id] ? '❤️' : '🤍'} Like
                            </button>
                            <button style={styles.commentBtn} onClick={() => toggleComments(article.id)}>
                                💬 Comments {openComments[article.id] ? '▲' : '▼'}
                            </button>
                            <button style={styles.saveBtn} onClick={() => handleSave(article.id)}>
                                🔖 Save
                            </button>
                        </div>

                        {openComments[article.id] && (
                            <div style={styles.commentsSection}>
                                <div style={styles.addComment}>
                                    <input
                                        style={styles.commentInput}
                                        type="text"
                                        placeholder="Write a comment..."
                                        value={newComment[article.id] || ''}
                                        onChange={(e) => setNewComment(prev => ({ ...prev, [article.id]: e.target.value }))}
                                    />
                                    <button style={styles.postBtn} onClick={() => handleAddComment(article.id)}>Post</button>
                                </div>
                                {(comments[article.id] || []).length === 0 ? (
                                    <p style={styles.noComments}>No comments yet!</p>
                                ) : (
                                    (comments[article.id] || []).map((comment) => (
                                        <div key={comment.id} style={styles.commentCard}>
                                            <p style={styles.commentContent}>{comment.content}</p>
                                            <span style={styles.commentMeta}>
                                                {new Date(comment.createdDate).toLocaleDateString()}
                                            </span>
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
    container: { maxWidth: '800px', margin: '0 auto' },
    card: { backgroundColor: 'white', padding: '20px', borderRadius: '10px', boxShadow: '0 2px 8px rgba(0,0,0,0.08)', marginBottom: '12px' },
    catPill: { display: 'inline-block', padding: '2px 10px', borderRadius: '12px', fontSize: '11px', color: '#e63946', backgroundColor: '#fff0f1', marginBottom: '6px' },
    articleTitle: { color: '#333', margin: '0 0 6px', fontSize: '16px', fontWeight: '500' },
    description: { color: '#666', fontSize: '14px', margin: '0 0 10px' },
    stats: { display: 'flex', gap: '16px', color: '#888', fontSize: '13px', marginBottom: '10px' },
    actions: { display: 'flex', gap: '8px', flexWrap: 'wrap' },
    likeBtn: { padding: '6px 14px', backgroundColor: 'white', color: '#333', border: '1px solid #ddd', borderRadius: '20px', cursor: 'pointer', fontSize: '12px' },
    likedBtn: { padding: '6px 14px', backgroundColor: '#fff0f1', color: '#e63946', border: '1px solid #e63946', borderRadius: '20px', cursor: 'pointer', fontSize: '12px' },
    commentBtn: { padding: '6px 14px', backgroundColor: '#f0f4f8', color: '#457b9d', border: '1px solid #d0dde8', borderRadius: '20px', cursor: 'pointer', fontSize: '12px' },
    saveBtn: { padding: '6px 14px', backgroundColor: '#f0faf8', color: '#2a9d8f', border: '1px solid #b0ddd6', borderRadius: '20px', cursor: 'pointer', fontSize: '12px' },
    commentsSection: { marginTop: '14px', borderTop: '1px solid #f0f0f0', paddingTop: '14px' },
    addComment: { display: 'flex', gap: '8px', marginBottom: '12px' },
    commentInput: { flex: 1, padding: '8px 12px', borderRadius: '20px', border: '1px solid #ddd', fontSize: '13px' },
    postBtn: { padding: '8px 16px', backgroundColor: '#e63946', color: 'white', border: 'none', borderRadius: '20px', cursor: 'pointer', fontSize: '12px' },
    commentCard: { backgroundColor: '#f8f9fa', padding: '10px 14px', borderRadius: '10px', marginBottom: '8px' },
    commentContent: { margin: '0 0 4px', color: '#333', fontSize: '13px' },
    commentMeta: { color: '#aaa', fontSize: '11px' },
    noComments: { color: '#aaa', textAlign: 'center', fontSize: '13px' },
    message: { textAlign: 'center', fontWeight: '500', color: 'green', marginBottom: '12px' },
    center: { textAlign: 'center', marginTop: '60px', fontSize: '16px', color: '#888' }
};

export default ArticlesPage;