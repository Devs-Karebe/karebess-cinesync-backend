CREATE TABLE users (
id UUID PRIMARY KEY,
name VARCHAR(255) NOT NULL,
email VARCHAR(255) NOT NULL UNIQUE,
password_hash VARCHAR(255) NOT NULL,
status VARCHAR(20) NOT NULL
    CHECK (
        status IN (
            'ACTIVE',
            'INACTIVE',
            'DELETED'
        )
    ),
created_at TIMESTAMP NOT NULL,
updated_at TIMESTAMP NOT NULL,
deleted_at TIMESTAMP NULL
);

CREATE TABLE refresh_tokens (
id UUID PRIMARY KEY,
token VARCHAR(512) NOT NULL UNIQUE,
user_id UUID NOT NULL,
expiration_date TIMESTAMP NOT NULL,
CONSTRAINT fk_refresh_tokens_user
    FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE
);

CREATE TABLE password_reset_tokens (
id UUID PRIMARY KEY,
token VARCHAR(512) NOT NULL UNIQUE,
user_id UUID NOT NULL,
expiration_date TIMESTAMP NOT NULL,
token_type VARCHAR(30) NOT NULL
    CHECK (
        token_type IN (
            'RESET_PASSWORD',
            'REACTIVATE_ACCOUNT'
        )
    ),

CONSTRAINT fk_password_reset_tokens_user
    FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE
);

CREATE INDEX idx_users_email
ON users(email);

CREATE INDEX idx_refresh_tokens_token
ON refresh_tokens(token);

CREATE INDEX idx_refresh_tokens_user_id
ON refresh_tokens(user_id);

CREATE INDEX idx_password_reset_tokens_token
ON password_reset_tokens(token);

CREATE INDEX idx_password_reset_tokens_user_id
ON password_reset_tokens(user_id);
