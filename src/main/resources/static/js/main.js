document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.app-alert').forEach(alert => {
        setTimeout(() => {
            alert.style.transition = 'opacity 0.4s ease';
            alert.style.opacity = '0';
            setTimeout(() => alert.remove(), 400);
        }, 3500);
    });

    const root = document.documentElement;
    const storageKey = 'rostream-theme';
    const metaTheme = document.querySelector('meta[name="theme-color"]');
    const mediaQuery = window.matchMedia('(prefers-color-scheme: light)');

    const getSystemTheme = () => mediaQuery.matches ? 'light' : 'dark';

    const syncThemeColor = (theme) => {
        if (!metaTheme) return;
        metaTheme.setAttribute('content', theme === 'light' ? '#fffaf1' : '#14161b');
    };

    const applyTheme = (theme, persist = true) => {
        const resolved = theme === 'light' ? 'light' : 'dark';
        root.setAttribute('data-theme', resolved);
        syncThemeColor(resolved);
        if (persist) {
            localStorage.setItem(storageKey, resolved);
        }
    };

    const savedTheme = localStorage.getItem(storageKey);
    applyTheme(savedTheme || getSystemTheme(), false);

    if (savedTheme) {
        localStorage.setItem(storageKey, savedTheme);
    }

    document.querySelectorAll('[data-theme-toggle]').forEach(button => {
        button.addEventListener('click', () => {
            const nextTheme = root.getAttribute('data-theme') === 'light' ? 'dark' : 'light';
            applyTheme(nextTheme);
        });
    });

    mediaQuery.addEventListener('change', () => {
        const stored = localStorage.getItem(storageKey);
        if (!stored) {
            applyTheme(getSystemTheme(), false);
        }
    });

    const navbar = document.querySelector('.app-navbar');
    const isHomePage = document.body.classList.contains('home-page');

    if (navbar && isHomePage) {
        const toggleNavbarScrolled = () => {
            if (window.scrollY > 24) {
                navbar.classList.add('navbar-scrolled');
            } else {
                navbar.classList.remove('navbar-scrolled');
            }
        };

        toggleNavbarScrolled();
        window.addEventListener('scroll', toggleNavbarScrolled, { passive: true });
    }
});
