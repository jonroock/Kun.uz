import React, { useState, useEffect } from 'react';
import axios from 'axios';

const API_URL = 'http://localhost:8080/api/v1';

function WriteArticlePage() {
    const [title, setTitle] = useState('');
    const [description, setDescription] = useState('');
    const [content, setContent] = useState('');
    const [readTime, setReadTime] = useState(5);
    const [regionId, setRegionId] = useState('');
    const [imageId, setImageId] = useState('');
    const [categoryList, setCategoryList] = useState([]);
    const [sectionList, setSectionList] = useState([]);
    const [selectedCategories, setSelectedCategories] = useState([]);
    const [selectedSections, setSelectedSections] = useState([]);
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');
    const [regions, setRegions] = useState([]);
    const [uploading, setUploading] = useState(false);

    useEffect(() => {
        loadCategories();
        loadSections();
        loadRegions();
    }, []);

    const loadRegions = async () => {
        try {
            const response = await axios.get(`${API_URL}/region/lang`, {
                headers: { 'Accept-Language': 'EN' }
            });
            setRegions(response.data);
        } catch (err) {
            console.error('Failed to load regions');
        }
    };

    const loadCategories = async () => {
        try {
            const response = await axios.get(`${API_URL}/category/lang`, {
                headers: { 'Accept-Language': 'EN' }
            });
            setCategoryList(response.data);
        } catch (err) {
            console.error('Failed to load categories');
        }
    };

    const loadSections = async () => {
        try {
            const response = await axios.get(`${API_URL}/section/lang`, {
                headers: { 'Accept-Language': 'EN' }
            });
            setSectionList(response.data);
        } catch (err) {
            console.error('Failed to load sections');
        }
    };

    const handleImageUpload = async (e) => {
        const file = e.target.files[0];
        if (!file) return;
        setUploading(true);
        try {
            const formData = new FormData();
            formData.append('file', file);
            const response = await axios.post(`${API_URL}/attach/upload`, formData);
            setImageId(response.data.id);
            setMessage('Image uploaded! ✅');
        } catch (err) {
            setError('Image upload failed!');
        } finally {
            setUploading(false);
        }
    };

    const handleCategoryChange = (id) => {
        setSelectedCategories(prev =>
            prev.includes(id) ? prev.filter(c => c !== id) : [...prev, id]
        );
    };

    const handleSectionChange = (id) => {
        setSelectedSections(prev =>
            prev.includes(id) ? prev.filter(s => s !== id) : [...prev, id]
        );
    };

    const handleSubmit = async () => {
        if (!title || !description || !content || !imageId || !regionId) {
            setError('Please fill all fields and upload an image!');
            return;
        }
        if (selectedCategories.length === 0 || selectedSections.length === 0) {
            setError('Please select at least one category and section!');
            return;
        }
        try {
            await axios.post(`${API_URL}/article/moderator`, {
                title,
                description,
                content,
                readTime: parseInt(readTime),
                regionId: parseInt(regionId),
                imageId,
                categoryList: selectedCategories.map(id => ({ id })),
                sectionList: selectedSections.map(id => ({ id }))
            });
            setMessage('Article created successfully! 🎉');
            setError('');
            // Reset form
            setTitle('');
            setDescription('');
            setContent('');
            setReadTime(5);
            setRegionId('');
            setImageId('');
            setSelectedCategories([]);
            setSelectedSections([]);
        } catch (err) {
            setError('Failed to create article!');
        }
    };

    return (
        <div style={styles.content}>
            <h3 style={styles.title}>✍️ Write Article</h3>

            {message && <p style={styles.success}>{message}</p>}
            {error && <p style={styles.error}>{error}</p>}

            <div style={styles.form}>
                {/* TITLE */}
                <div style={styles.field}>
                    <label style={styles.label}>Title *</label>
                    <input
                        style={styles.input}
                        type="text"
                        placeholder="Article title"
                        value={title}
                        onChange={(e) => setTitle(e.target.value)}
                    />
                </div>

                {/* DESCRIPTION */}
                <div style={styles.field}>
                    <label style={styles.label}>Description *</label>
                    <input
                        style={styles.input}
                        type="text"
                        placeholder="Short description"
                        value={description}
                        onChange={(e) => setDescription(e.target.value)}
                    />
                </div>

                {/* CONTENT */}
                <div style={styles.field}>
                    <label style={styles.label}>Content *</label>
                    <textarea
                        style={styles.textarea}
                        placeholder="Write your article content here..."
                        value={content}
                        onChange={(e) => setContent(e.target.value)}
                        rows={8}
                    />
                </div>

                {/* READ TIME */}
                <div style={styles.field}>
                    <label style={styles.label}>Read Time (minutes) *</label>
                    <input
                        style={styles.input}
                        type="number"
                        min="1"
                        value={readTime}
                        onChange={(e) => setReadTime(e.target.value)}
                    />
                </div>

                {/* REGION */}
                <div style={styles.field}>
                    <label style={styles.label}>Region *</label>
                    <select
                        style={styles.input}
                        value={regionId}
                        onChange={(e) => setRegionId(e.target.value)}>
                        <option value="">Select region</option>
                        {regions.map(r => (
                            <option key={r.id} value={r.id}>{r.nameEn}</option>
                        ))}
                    </select>
                </div>

                {/* IMAGE */}
                <div style={styles.field}>
                    <label style={styles.label}>Image *</label>
                    <input
                        type="file"
                        accept="image/*"
                        onChange={handleImageUpload}
                        style={styles.fileInput}
                    />
                    {uploading && <p style={styles.uploading}>Uploading...</p>}
                    {imageId && <p style={styles.uploaded}>✅ Image uploaded!</p>}
                </div>

                {/* CATEGORIES */}
                <div style={styles.field}>
                    <label style={styles.label}>Categories *</label>
                    <div style={styles.checkboxGroup}>
                        {categoryList.map(cat => (
                            <label key={cat.id} style={styles.checkboxLabel}>
                                <input
                                    type="checkbox"
                                    checked={selectedCategories.includes(cat.id)}
                                    onChange={() => handleCategoryChange(cat.id)}
                                />
                                {cat.nameEn}
                            </label>
                        ))}
                    </div>
                </div>

                {/* SECTIONS */}
                <div style={styles.field}>
                    <label style={styles.label}>Sections *</label>
                    <div style={styles.checkboxGroup}>
                        {sectionList.map(sec => (
                            <label key={sec.id} style={styles.checkboxLabel}>
                                <input
                                    type="checkbox"
                                    checked={selectedSections.includes(sec.id)}
                                    onChange={() => handleSectionChange(sec.id)}
                                />
                                {sec.nameEn}
                            </label>
                        ))}
                    </div>
                </div>

                {/* SUBMIT */}
                <button style={styles.submitBtn} onClick={handleSubmit}>
                    🚀 Publish Article
                </button>
            </div>
        </div>
    );
}

const styles = {
    content: {
        maxWidth: '700px',
        margin: '30px auto',
        padding: '0 20px'
    },
    title: {
        color: '#333',
        marginBottom: '20px'
    },
    form: {
        backgroundColor: 'white',
        padding: '30px',
        borderRadius: '10px',
        boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
        display: 'flex',
        flexDirection: 'column',
        gap: '20px'
    },
    field: {
        display: 'flex',
        flexDirection: 'column',
        gap: '8px'
    },
    label: {
        fontWeight: 'bold',
        color: '#333',
        fontSize: '14px'
    },
    input: {
        padding: '12px',
        borderRadius: '6px',
        border: '1px solid #ddd',
        fontSize: '14px'
    },
    textarea: {
        padding: '12px',
        borderRadius: '6px',
        border: '1px solid #ddd',
        fontSize: '14px',
        resize: 'vertical'
    },
    fileInput: {
        padding: '8px 0'
    },
    checkboxGroup: {
        display: 'flex',
        flexWrap: 'wrap',
        gap: '15px'
    },
    checkboxLabel: {
        display: 'flex',
        alignItems: 'center',
        gap: '5px',
        cursor: 'pointer',
        fontSize: '14px'
    },
    submitBtn: {
        padding: '14px',
        backgroundColor: '#e63946',
        color: 'white',
        border: 'none',
        borderRadius: '6px',
        fontSize: '16px',
        cursor: 'pointer',
        fontWeight: 'bold'
    },
    success: { color: 'green', fontWeight: 'bold' },
    error: { color: 'red', fontWeight: 'bold' },
    uploading: { color: '#888', fontSize: '13px' },
    uploaded: { color: 'green', fontSize: '13px' }
};

export default WriteArticlePage;