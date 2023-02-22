import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('Setting e2e test', () => {
  const settingPageUrl = '/setting-my-suffix';
  const settingPageUrlPattern = new RegExp('/setting-my-suffix(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const settingSample = {};

  let setting;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/settings+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/settings').as('postEntityRequest');
    cy.intercept('DELETE', '/api/settings/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (setting) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/settings/${setting.id}`,
      }).then(() => {
        setting = undefined;
      });
    }
  });

  it('Settings menu should load Settings page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('setting-my-suffix');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Setting').should('exist');
    cy.url().should('match', settingPageUrlPattern);
  });

  describe('Setting page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(settingPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Setting page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/setting-my-suffix/new$'));
        cy.getEntityCreateUpdateHeading('Setting');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', settingPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/settings',
          body: settingSample,
        }).then(({ body }) => {
          setting = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/settings+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/settings?page=0&size=20>; rel="last",<http://localhost/api/settings?page=0&size=20>; rel="first"',
              },
              body: [setting],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(settingPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Setting page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('setting');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', settingPageUrlPattern);
      });

      it('edit button click should load edit Setting page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Setting');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', settingPageUrlPattern);
      });

      it('edit button click should load edit Setting page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Setting');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', settingPageUrlPattern);
      });

      it('last delete button click should delete instance of Setting', () => {
        cy.intercept('GET', '/api/settings/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('setting').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', settingPageUrlPattern);

        setting = undefined;
      });
    });
  });

  describe('new Setting page', () => {
    beforeEach(() => {
      cy.visit(`${settingPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Setting');
    });

    it('should create an instance of Setting', () => {
      cy.get(`[data-cy="name"]`).type('Multi-layered fuchsia').should('have.value', 'Multi-layered fuchsia');

      cy.get(`[data-cy="valueType"]`).select('DOUBLE');

      cy.get(`[data-cy="expressionType"]`).select('COMPLEX');

      cy.get(`[data-cy="value"]`).type('Agent Rustic Eritrea').should('have.value', 'Agent Rustic Eritrea');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        setting = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', settingPageUrlPattern);
    });
  });
});
