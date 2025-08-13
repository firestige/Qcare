// Zustand适配器实现
import { create } from 'zustand';
import { devtools, persist } from 'zustand/middleware';
import { immer } from 'zustand/middleware/immer';
import type { StateManager, StoreCreator, UseStore } from '../types';

class ZustandAdapter implements StateManager {
  create<T>(creator: StoreCreator<T>): UseStore<T> {
    const store = create<T>()(
      immer((set, get) => {
        const setState = (partial: Partial<T> | ((state: T) => Partial<T>)) => {
          if (typeof partial === 'function') {
            set((state) => {
              const updates = partial(state as T);
              Object.keys(updates as object).forEach((key) => {
                (state as any)[key] = (updates as any)[key];
              });
            });
          } else {
            set((state) => {
              Object.keys(partial as object).forEach((key) => {
                (state as any)[key] = (partial as any)[key];
              });
            });
          }
        };

        return creator(setState, get);
      })
    );

    return store as UseStore<T>;
  }

  createWithDevtools<T>(creator: StoreCreator<T>, name?: string): UseStore<T> {
    const store = create<T>()(
      devtools(
        immer((set, get) => {
          const setState = (
            partial: Partial<T> | ((state: T) => Partial<T>)
          ) => {
            if (typeof partial === 'function') {
              set((state) => {
                const updates = partial(state as T);
                Object.keys(updates as object).forEach((key) => {
                  (state as any)[key] = (updates as any)[key];
                });
              });
            } else {
              set((state) => {
                Object.keys(partial as object).forEach((key) => {
                  (state as any)[key] = (partial as any)[key];
                });
              });
            }
          };

          return creator(setState, get);
        }),
        { name: name || 'AppStore' }
      )
    );

    return store as UseStore<T>;
  }

  createPersisted<T>(
    creator: StoreCreator<T>,
    options: { name: string; version?: number }
  ): UseStore<T> {
    const store = create<T>()(
      persist(
        immer((set, get) => {
          const setState = (
            partial: Partial<T> | ((state: T) => Partial<T>)
          ) => {
            if (typeof partial === 'function') {
              set((state) => {
                const updates = partial(state as T);
                Object.keys(updates as object).forEach((key) => {
                  (state as any)[key] = (updates as any)[key];
                });
              });
            } else {
              set((state) => {
                Object.keys(partial as object).forEach((key) => {
                  (state as any)[key] = (partial as any)[key];
                });
              });
            }
          };

          return creator(setState, get);
        }),
        {
          name: options.name,
          version: options.version || 1,
        }
      )
    );

    return store as UseStore<T>;
  }
}

export default ZustandAdapter;
