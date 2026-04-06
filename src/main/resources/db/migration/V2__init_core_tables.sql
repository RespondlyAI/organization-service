-- V2__init_core_tables.sql

CREATE TABLE subscription_plans (
    id UUID PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    max_agents INTEGER NOT NULL,
    max_ai_calls INTEGER NOT NULL
);

-- PostgreSQL requires explicitly defining custom Enum types
CREATE TYPE subscription_status_enum AS ENUM ('trialing', 'active', 'suspended', 'cancelled');
CREATE TYPE org_status_enum AS ENUM ('active', 'suspended', 'deleted');

-- The Core Organization Table
CREATE TABLE organizations (
    id UUID PRIMARY KEY,
    industry_id UUID REFERENCES industries(id),
    name VARCHAR(255) NOT NULL,
    organization_type_id UUID REFERENCES organization_types(id),
    subscription_status subscription_status_enum NOT NULL,
    description TEXT,
    status org_status_enum NOT NULL,
    created_by_user_id VARCHAR(255) NOT NULL, -- This links to your Auth Service
    subscription_plan_id UUID REFERENCES subscription_plans(id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE organization_websites (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    website_url VARCHAR(255) NOT NULL
);