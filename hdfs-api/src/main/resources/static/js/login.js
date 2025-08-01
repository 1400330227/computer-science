/**
 * 登录相关的JavaScript功能
 */

// 处理用户登录
function login(username, password) {
    // 准备请求数据
    const loginData = {
        username: username,
        password: password
    };

    // 发送登录请求
    fetch('/api/users/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(loginData),
        credentials: 'include' // 确保包含cookie
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('登录失败');
        }
        return response.json();
    })
    .then(data => {
        if (data.success) {
            // 登录成功，保存用户信息到localStorage
            localStorage.setItem('currentUser', JSON.stringify({
                userId: data.userId,
                account: data.account,
                userType: data.userType
            }));
            
            // 重定向到首页或者其他页面
            window.location.href = '/index.html';
        } else {
            // 显示登录失败消息
            alert(data.message || '登录失败，请检查用户名和密码');
        }
    })
    .catch(error => {
        console.error('登录错误:', error);
        alert('登录失败: ' + error.message);
    });
}

// 处理用户登出
function logout() {
    fetch('/api/users/logout', {
        method: 'POST',
        credentials: 'include' // 确保包含cookie
    })
    .then(response => response.json())
    .then(data => {
        // 清除本地存储的用户信息
        localStorage.removeItem('currentUser');
        
        // 重定向到登录页
        window.location.href = '/login.html';
    })
    .catch(error => {
        console.error('登出错误:', error);
    });
}

// 检查用户登录状态
function checkLoginStatus() {
    // 从localStorage检查用户信息
    const userStr = localStorage.getItem('currentUser');
    if (userStr) {
        try {
            const user = JSON.parse(userStr);
            return user;
        } catch (e) {
            console.error('解析用户信息失败', e);
        }
    }
    
    // 如果localStorage中没有，尝试调用API验证
    return fetch('/api/users/current', {
        credentials: 'include' // 确保包含cookie
    })
    .then(response => {
        if (response.ok) {
            return response.json();
        }
        throw new Error('未登录');
    })
    .then(data => {
        // 更新本地存储
        localStorage.setItem('currentUser', JSON.stringify({
            userId: data.userId,
            account: data.account,
            userType: data.userType
        }));
        return data;
    })
    .catch(() => {
        // 如果API调用失败，表示用户未登录
        return null;
    });
}

// 页面加载时检查登录状态
document.addEventListener('DOMContentLoaded', function() {
    // 检查是否在登录页面
    if (window.location.pathname !== '/login.html') {
        checkLoginStatus().then(user => {
            if (!user) {
                // 如果未登录且不在登录页，重定向到登录页
                window.location.href = '/login.html';
            } else {
                // 已登录，可以更新页面上的用户信息显示
                const userDisplayEl = document.getElementById('userDisplay');
                if (userDisplayEl) {
                    userDisplayEl.textContent = `欢迎, ${user.account}`;
                }
            }
        });
    }
});

// 登录表单提交处理
document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;
            login(username, password);
        });
    }

    // 登出按钮处理
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function(e) {
            e.preventDefault();
            logout();
        });
    }
}); 