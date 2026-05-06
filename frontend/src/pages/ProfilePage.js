import React from 'react';

function ProfilePage({ user }) {

    return (
        <div style={styles.content}>
            <div style={styles.card}>
                <div style={styles.avatar}>

                    {user.name ? user.name.charAt(0) : '?'}
                    {user.surname ? user.surname.charAt(0) : '?'}
                </div>
                <h2 style={styles.name}>{user.name} {user.surname}</h2>
                <p style={styles.email}>📧 {user.username}</p>
                <div style={styles.roleContainer}>
                    {user.roleList && user.roleList.map((role, index) => (
                        <span key={index} style={styles.role}>
                            {role.replace('ROLE_', '')}
                        </span>
                    ))}
                </div>
            </div>
        </div>
    );
}

const styles = {
    content: {
        maxWidth: '500px',
        margin: '50px auto',
        padding: '0 20px'
    },
    card: {
        backgroundColor: 'white',
        padding: '40px',
        borderRadius: '15px',
        boxShadow: '0 2px 10px rgba(0,0,0,0.1)',
        textAlign: 'center'
    },
    avatar: {
        width: '80px',
        height: '80px',
        borderRadius: '50%',
        backgroundColor: '#e63946',
        color: 'white',
        fontSize: '28px',
        fontWeight: 'bold',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        margin: '0 auto 20px'
    },
    name: { color: '#333', margin: '0 0 10px' },
    email: { color: '#666', margin: '0 0 20px' },
    roleContainer: {
        display: 'flex',
        justifyContent: 'center',
        gap: '10px'
    },
    role: {
        backgroundColor: '#e63946',
        color: 'white',
        padding: '5px 15px',
        borderRadius: '20px',
        fontSize: '12px',
        fontWeight: 'bold'
    }
};

export default ProfilePage;