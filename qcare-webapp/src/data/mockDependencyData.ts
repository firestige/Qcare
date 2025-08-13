// 依赖管理mock数据
import type {
  DependencyStats,
  DependencyTreeNode,
  DependencyItem,
} from '../types/dependency';

export const mockDependencyStats: DependencyStats = {
  totalDependencies: 156,
  safeDependencies: 89,
  outdatedDependencies: 45,
  vulnerableDependencies: 22,
};

export const mockDependencyTree: DependencyTreeNode[] = [
  {
    title: 'icare-microservice',
    key: '0-0',
    children: [
      {
        title: 'user-service',
        key: '0-0-0',
        children: [
          {
            title: 'spring-boot-starter-web:2.7.0',
            key: '0-0-0-0',
          },
          {
            title: 'mysql-connector-java:8.0.28',
            key: '0-0-0-1',
          },
          {
            title: 'redis-client:3.3.0',
            key: '0-0-0-2',
          },
        ],
      },
      {
        title: 'order-service',
        key: '0-0-1',
        children: [
          {
            title: 'spring-boot-starter-web:2.7.0',
            key: '0-0-1-0',
          },
          {
            title: 'spring-cloud-starter-feign:1.4.7',
            key: '0-0-1-1',
          },
          {
            title: 'postgresql:42.3.1',
            key: '0-0-1-2',
          },
        ],
      },
      {
        title: 'payment-service',
        key: '0-0-2',
        children: [
          {
            title: 'spring-boot-starter-web:2.6.8',
            key: '0-0-2-0',
          },
          {
            title: 'spring-security:5.7.1',
            key: '0-0-2-1',
          },
        ],
      },
    ],
  },
];

export const mockDependencyData: DependencyItem[] = [
  {
    key: '1',
    name: 'spring-boot-starter-web',
    version: '2.7.0',
    latestVersion: '2.7.14',
    status: 'outdated',
    security: 'safe',
    license: 'Apache 2.0',
    size: '1.2MB',
    usedBy: ['user-service', 'order-service'],
    description: 'Spring Boot Web Starter for building web applications',
    homepage: 'https://spring.io/projects/spring-boot',
    repository: 'https://github.com/spring-projects/spring-boot',
    lastUpdate: '2023-07-15',
  },
  {
    key: '2',
    name: 'mysql-connector-java',
    version: '8.0.28',
    latestVersion: '8.0.33',
    status: 'outdated',
    security: 'safe',
    license: 'GPL 2.0',
    size: '2.1MB',
    usedBy: ['user-service'],
    description: 'MySQL JDBC driver for Java applications',
    homepage: 'https://dev.mysql.com/downloads/connector/j/',
    repository: 'https://github.com/mysql/mysql-connector-j',
    lastUpdate: '2023-08-20',
  },
  {
    key: '3',
    name: 'redis-client',
    version: '3.3.0',
    latestVersion: '4.1.0',
    status: 'outdated',
    security: 'warning',
    license: 'MIT',
    size: '850KB',
    usedBy: ['user-service'],
    description: 'Redis client library for Java',
    homepage: 'https://redis.io/',
    repository: 'https://github.com/redis/jedis',
    lastUpdate: '2023-09-10',
  },
  {
    key: '4',
    name: 'spring-cloud-starter-feign',
    version: '1.4.7',
    latestVersion: '3.1.3',
    status: 'critical',
    security: 'vulnerable',
    license: 'Apache 2.0',
    size: '3.2MB',
    usedBy: ['order-service'],
    description: 'Spring Cloud Feign for microservice communication',
    homepage: 'https://spring.io/projects/spring-cloud-openfeign',
    repository: 'https://github.com/spring-cloud/spring-cloud-openfeign',
    lastUpdate: '2023-06-01',
  },
  {
    key: '5',
    name: 'postgresql',
    version: '42.3.1',
    latestVersion: '42.6.0',
    status: 'outdated',
    security: 'safe',
    license: 'BSD-2-Clause',
    size: '1.8MB',
    usedBy: ['order-service'],
    description: 'PostgreSQL JDBC driver',
    homepage: 'https://postgresql.org/',
    repository: 'https://github.com/pgjdbc/pgjdbc',
    lastUpdate: '2023-07-28',
  },
  {
    key: '6',
    name: 'spring-security',
    version: '5.7.1',
    latestVersion: '5.8.4',
    status: 'outdated',
    security: 'safe',
    license: 'Apache 2.0',
    size: '4.5MB',
    usedBy: ['payment-service', 'user-service'],
    description:
      'Spring Security framework for authentication and authorization',
    homepage: 'https://spring.io/projects/spring-security',
    repository: 'https://github.com/spring-projects/spring-security',
    lastUpdate: '2023-08-05',
  },
];
