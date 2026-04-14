-- Migration: add user-targeting rules for vouchers while preserving old voucher data.
-- Run manually because this project keeps `spring.jpa.hibernate.ddl-auto=none`.

SET @schema_name := DATABASE();

SET @sql := (
    SELECT IF(
        EXISTS(
            SELECT 1
            FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_SCHEMA = @schema_name
              AND TABLE_NAME = 'vouchers'
              AND COLUMN_NAME = 'auto_display_enabled'
        ),
        'SELECT ''auto_display_enabled already exists''',
        'ALTER TABLE vouchers ADD COLUMN auto_display_enabled TINYINT(1) NOT NULL DEFAULT 0 AFTER active'
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql := (
    SELECT IF(
        EXISTS(
            SELECT 1
            FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_SCHEMA = @schema_name
              AND TABLE_NAME = 'vouchers'
              AND COLUMN_NAME = 'min_total_spent_amount'
        ),
        'SELECT ''min_total_spent_amount already exists''',
        'ALTER TABLE vouchers ADD COLUMN min_total_spent_amount DECIMAL(15,2) NOT NULL DEFAULT 0.00 AFTER auto_display_enabled'
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql := (
    SELECT IF(
        EXISTS(
            SELECT 1
            FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_SCHEMA = @schema_name
              AND TABLE_NAME = 'vouchers'
              AND COLUMN_NAME = 'min_account_age_days'
        ),
        'SELECT ''min_account_age_days already exists''',
        'ALTER TABLE vouchers ADD COLUMN min_account_age_days INT NOT NULL DEFAULT 0 AFTER min_total_spent_amount'
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql := (
    SELECT IF(
        EXISTS(
            SELECT 1
            FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_SCHEMA = @schema_name
              AND TABLE_NAME = 'vouchers'
              AND COLUMN_NAME = 'audience_match_mode'
        ),
        'SELECT ''audience_match_mode already exists''',
        'ALTER TABLE vouchers ADD COLUMN audience_match_mode VARCHAR(10) NOT NULL DEFAULT ''ALL'' AFTER min_account_age_days'
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

UPDATE vouchers
SET auto_display_enabled = COALESCE(auto_display_enabled, 0),
    min_total_spent_amount = COALESCE(min_total_spent_amount, 0.00),
    min_account_age_days = COALESCE(min_account_age_days, 0),
    audience_match_mode = COALESCE(NULLIF(audience_match_mode, ''), 'ALL');

SET @sql := (
    SELECT IF(
        EXISTS(
            SELECT 1
            FROM INFORMATION_SCHEMA.STATISTICS
            WHERE TABLE_SCHEMA = @schema_name
              AND TABLE_NAME = 'vouchers'
              AND INDEX_NAME = 'idx_vouchers_auto_display'
        ),
        'SELECT ''idx_vouchers_auto_display already exists''',
        'CREATE INDEX idx_vouchers_auto_display ON vouchers (auto_display_enabled, active, start_at, end_at)'
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Example 1:
-- Voucher only shows to users who have spent at least 1,000,000 VND.
-- UPDATE vouchers
-- SET auto_display_enabled = 1,
--     min_total_spent_amount = 1000000.00,
--     min_account_age_days = 0,
--     audience_match_mode = 'ALL'
-- WHERE code = 'VIP1000K';

-- Example 2:
-- Voucher shows when user has spent at least 1,000,000 VND OR account age is at least 365 days.
-- UPDATE vouchers
-- SET auto_display_enabled = 1,
--     min_total_spent_amount = 1000000.00,
--     min_account_age_days = 365,
--     audience_match_mode = 'ANY'
-- WHERE code = 'LOYAL365';
